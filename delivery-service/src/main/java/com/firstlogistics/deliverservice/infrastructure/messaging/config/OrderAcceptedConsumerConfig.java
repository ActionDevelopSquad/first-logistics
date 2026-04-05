package com.firstlogistics.deliverservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.exception.DistributedLockException;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.OrderAcceptedRecoverer;
import com.firstlogistics.deliverservice.domain.event.OrderAcceptedEvent;
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

@Configuration
@RequiredArgsConstructor
public class OrderAcceptedConsumerConfig {

	private final KafkaConsumerConfig kafkaConsumerConfig;
	private final ObjectMapper objectMapper;

	@Bean
	public ConsumerFactory<String, OrderAcceptedEvent> orderAcceptedConsumerFactory() {
		JsonDeserializer<OrderAcceptedEvent> deserializer = new JsonDeserializer<>(OrderAcceptedEvent.class, objectMapper);
		deserializer.addTrustedPackages("com.firstlogistics.deliverservice.domain.event");
		deserializer.setUseTypeHeaders(false);
		return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig.commonConsumerProps(), new StringDeserializer(), deserializer);
	}

	@Bean
	public DefaultErrorHandler orderAcceptedErrorHandler(OrderAcceptedRecoverer recoverer) {
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
		errorHandler.addNotRetryableExceptions(DistributedLockException.class);
		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OrderAcceptedEvent> orderAcceptedListenerContainerFactory(
		DefaultErrorHandler orderAcceptedErrorHandler
	) {
		ConcurrentKafkaListenerContainerFactory<String, OrderAcceptedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(orderAcceptedConsumerFactory());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		factory.setCommonErrorHandler(orderAcceptedErrorHandler);
		return factory;
	}
}
