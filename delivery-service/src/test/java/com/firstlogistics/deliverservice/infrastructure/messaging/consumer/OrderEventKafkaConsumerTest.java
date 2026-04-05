package com.firstlogistics.deliverservice.infrastructure.messaging.consumer;

import com.firstlogistics.deliverservice.application.DeliveryQueryService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.facade.DeliveryCommandFacade;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import com.firstlogistics.deliverservice.infrastructure.feign.UserClient;
import com.firstlogistics.deliverservice.infrastructure.exception.InfraErrorCode;
import com.firstlogistics.deliverservice.infrastructure.exception.InfraException;
import com.firstlogistics.deliverservice.infrastructure.messaging.config.KafkaConsumerConfig;
import com.firstlogistics.deliverservice.domain.event.OrderAcceptedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@Slf4j
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,common.security.config.SecurityConfig,common.security.config.CommonSecurityAutoConfig,common.security.config.FeignAuthPropagationConfig"
)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"order.accepted", "delivery.creation.failed", "order.accepted.DLT"},
		bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class OrderEventKafkaConsumerTest {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private KafkaConsumerConfig kafkaConsumerConfig;

	@MockitoBean
	private DeliveryCommandFacade deliveryCommandFacade;
	@MockitoBean
	private DeliveryQueryService deliveryQueryService;
	@MockitoBean
	private RedissonClient redissonClient;
	@MockitoBean
	private UserClient userClient;
	@MockitoBean
	private CompanyClient companyClient;
	@MockitoBean
	private HubClient hubClient;

	@Nested
	@DisplayName("handleOrderAccepted")
	class HandleOrderAccepted {

		@Test
		@DisplayName("정상 처리 - createDelivery 호출 후 ack")
		void success() {
			// given
			OrderAcceptedEvent event = createEvent();
			given(deliveryQueryService.existsByOrderId(event.orderId())).willReturn(false);

			// when
			kafkaTemplate.send("order.accepted", event.orderId().toString(), event);
			log.info("Kafka send: orderId={}", event.orderId());

			// then
			await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
					then(deliveryCommandFacade).should().createDeliveryBySystem(any(CreateDeliveryCommand.class))
			);
		}

		@Test
		@DisplayName("중복 메시지 - createDelivery 호출 안 함")
		void idempotency() {
			// given
			OrderAcceptedEvent event = createEvent();
			given(deliveryQueryService.existsByOrderId(event.orderId())).willReturn(true);

			// when
			kafkaTemplate.send("order.accepted", event.orderId().toString(), event);
			log.info("Kafka send: orderId={}", event.orderId());

			// then
			// existsByOrderId 호출 확인과 never() 단언을 같은 await 블록 안에서 평가한다.
			// never()를 await 밖에서 즉시 평가하면 리스너 스레드가 아직 실행 중일 수 있어
			// createDelivery 호출 여부를 확정하기 전에 단언이 통과하는 레이스 컨디션이 발생한다.
			await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
				then(deliveryQueryService).should().existsByOrderId(event.orderId());
				then(deliveryCommandFacade).should(never()).createDeliveryBySystem(any());
			});
		}

		@Test
		@DisplayName("DeliveryException - delivery.creation.failed Saga 보상 발행")
		void deliveryException_publishesSagaCompensation() {
			// given
			OrderAcceptedEvent event = createEvent();
			given(deliveryQueryService.existsByOrderId(event.orderId())).willReturn(false);
			given(deliveryCommandFacade.createDeliveryBySystem(any()))
					.willThrow(new InfraException(InfraErrorCode.HUB_NOT_FOUND));

			// subscribe 대신 assign + seekToBeginning: 그룹 조인 없이 바로 파티션 읽기 (타이밍 문제 방지)
			try (KafkaConsumer<String, String> sagaConsumer = createTestConsumer("saga-test")) {
				TopicPartition tp = new TopicPartition("delivery.creation.failed", 0);
				sagaConsumer.assign(List.of(tp));
				sagaConsumer.seekToBeginning(List.of(tp));

				// when
				kafkaTemplate.send("order.accepted", event.orderId().toString(), event);
				log.info("Kafka send: orderId={}", event.orderId());

				// then
				await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
					log.info("Await checking...");
					ConsumerRecords<String, String> records = sagaConsumer.poll(Duration.ofMillis(500));
					assertThat(records.isEmpty()).isFalse();
					var record = records.iterator().next();
					assertThat(record.key()).isEqualTo(event.orderId().toString());
					assertThat(record.value()).contains(event.orderId().toString());
				});
			}
		}
	}

	private KafkaConsumer<String, String> createTestConsumer(String groupIdPrefix) {
		Map<String, Object> props = new HashMap<>(kafkaConsumerConfig.commonConsumerProps());
		props.put("group.id", groupIdPrefix + "-" + UUID.randomUUID());
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		return new KafkaConsumer<>(props);
	}

	private OrderAcceptedEvent createEvent() {
		return new OrderAcceptedEvent(
				UUID.randomUUID(),
				LocalDateTime.now(),
				LocalDateTime.now().plusDays(3),
				"빠른 배송 부탁드립니다.",
				new OrderAcceptedEvent.SupplierInfo(UUID.randomUUID(), UUID.randomUUID()),
				new OrderAcceptedEvent.ReceiverInfo(UUID.randomUUID(), UUID.randomUUID(), "서울시 강남구 테헤란로 123", "101호"),
				List.of(new OrderAcceptedEvent.OrderItemInfo(UUID.randomUUID(), "마른 오징어", 50, 10000L))
		);
	}
}
