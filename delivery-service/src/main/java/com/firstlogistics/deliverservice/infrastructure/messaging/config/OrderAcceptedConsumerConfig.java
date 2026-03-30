package com.firstlogistics.deliverservice.infrastructure.messaging.config;

import com.firstlogistics.deliverservice.domain.exception.DeliveryCreationException;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.OrderAcceptedRecoverer;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

/**
 * order.accepted 토픽 컨슈머 설정
 * 새 컨슈머 추가 시 이 파일을 참고해 동일한 구조로 작성하세요.
 */
@Configuration
@RequiredArgsConstructor
public class OrderAcceptedConsumerConfig {

	private final KafkaConsumerConfig kafkaConsumerConfig;

	@Bean
	public ConsumerFactory<String, OrderAcceptedEvent> orderAcceptedConsumerFactory() {
		JsonDeserializer<OrderAcceptedEvent> deserializer = new JsonDeserializer<>(OrderAcceptedEvent.class);
		deserializer.addTrustedPackages("*");
		// 프로듀서가 보낸 타입 헤더 무시 → 항상 OrderAcceptedEvent로 역직렬화 (타입 불일치 오류 방지)
		deserializer.setUseTypeHeaders(false);
		return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig.commonConsumerProps(), new StringDeserializer(), deserializer);
	}

	@Bean
	public DefaultErrorHandler orderAcceptedErrorHandler(OrderAcceptedRecoverer recoverer) {
		// 1초 간격으로 최대 3회 재시도
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
		// 확정적 비즈니스 실패는 재시도 없이 즉시 recoverer (재시도해도 결과 동일)
		errorHandler.addNotRetryableExceptions(DeliveryCreationException.class, DeliveryException.class);
		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OrderAcceptedEvent> orderAcceptedListenerContainerFactory(
		DefaultErrorHandler orderAcceptedErrorHandler
	) {
		ConcurrentKafkaListenerContainerFactory<String, OrderAcceptedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(orderAcceptedConsumerFactory());
		// 처리 완료 후 수동으로 offset 커밋 (중복 처리 방지)
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		factory.setCommonErrorHandler(orderAcceptedErrorHandler);
		return factory;
	}
}
