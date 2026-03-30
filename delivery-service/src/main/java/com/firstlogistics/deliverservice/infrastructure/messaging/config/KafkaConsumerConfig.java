package com.firstlogistics.deliverservice.infrastructure.messaging.config;

import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ConsumerFactory<String, OrderAcceptedEvent> deliveryConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "delivery-service");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		// 자동 커밋 비활성화 (MANUAL ack 사용)
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		// 커밋된 메시지만 읽기 - 프로듀서에 transactional.id 설정 시 exactly-once 보장
		// 현재는 at-least-once + 컨슈머 중복 검증으로 처리하므로 미사용
		// props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
		// 처리 시간이 길어져도 consumer group에서 제외되지 않도록 설정
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

		JsonDeserializer<OrderAcceptedEvent> deserializer = new JsonDeserializer<>(OrderAcceptedEvent.class);
		deserializer.addTrustedPackages("*");

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
	}

	@Bean
	public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
		// 3회 재시도 소진 시 DLQ(order.accepted.DLT)로 발행
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
		// 1초 간격으로 최대 3회 재시도
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
		// 비즈니스 예외는 재시도 없이 즉시 DLQ로 (재시도해도 결과 동일)
		errorHandler.addNotRetryableExceptions(DeliveryException.class);
		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OrderAcceptedEvent> deliveryListenerContainerFactory(
		DefaultErrorHandler kafkaErrorHandler
	) {
		ConcurrentKafkaListenerContainerFactory<String, OrderAcceptedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(deliveryConsumerFactory());
		// 처리 완료 후 수동으로 offset 커밋 (중복 처리 방지)
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		factory.setCommonErrorHandler(kafkaErrorHandler);
		return factory;
	}
}
