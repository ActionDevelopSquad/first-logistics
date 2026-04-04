package com.firstlogistics.deliverservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.deliverservice.domain.event.OrderCancelledEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.messaging.producer.DeliveryEventKafkaProducer;
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
public class OrderCancelledConsumerConfig {

	private final KafkaConsumerConfig kafkaConsumerConfig;
	private final ObjectMapper objectMapper;

	@Bean
	public ConsumerFactory<String, OrderCancelledEvent> orderCancelledConsumerFactory() {
		JsonDeserializer<OrderCancelledEvent> deserializer = new JsonDeserializer<>(OrderCancelledEvent.class, objectMapper);
		deserializer.addTrustedPackages("com.firstlogistics.deliverservice.domain.event");
		deserializer.setUseTypeHeaders(false);
		return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig.commonConsumerProps(), new StringDeserializer(), deserializer);
	}

	@Bean
	public DefaultErrorHandler orderCancelledErrorHandler(DeliveryEventKafkaProducer deliveryEventKafkaProducer) {
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(
			(record, exception) -> {
				deliveryEventKafkaProducer.handleOrderCancelledDlt(String.valueOf(record.key()), record.value());
			},
			new FixedBackOff(1000L, 3L)
		);
		errorHandler.addNotRetryableExceptions(DeliveryException.class);
		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> orderCancelledListenerContainerFactory(
		DefaultErrorHandler orderCancelledErrorHandler
	) {
		ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(orderCancelledConsumerFactory());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		factory.setCommonErrorHandler(orderCancelledErrorHandler);
		return factory;
	}
}
