package com.firstlogistics.deliverservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.deliverservice.domain.event.UserStatusChangedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.UserStatusChangedRecoverer;
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
public class UserStatusChangedConsumerConfig {

	private final KafkaConsumerConfig kafkaConsumerConfig;
	private final ObjectMapper objectMapper;

	@Bean
	public ConsumerFactory<String, UserStatusChangedEvent> userStatusChangedConsumerFactory() {
		JsonDeserializer<UserStatusChangedEvent> deserializer =
			new JsonDeserializer<>(UserStatusChangedEvent.class, objectMapper);
		deserializer.addTrustedPackages("com.firstlogistics.deliverservice.domain.event");
		deserializer.setUseTypeHeaders(false);
		return new DefaultKafkaConsumerFactory<>(
			kafkaConsumerConfig.commonConsumerProps(), new StringDeserializer(), deserializer);
	}

	@Bean
	public DefaultErrorHandler userStatusChangedErrorHandler(UserStatusChangedRecoverer recoverer) {
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
		errorHandler.addNotRetryableExceptions();
		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserStatusChangedEvent> userStatusChangedListenerContainerFactory(
		DefaultErrorHandler userStatusChangedErrorHandler
	) {
		ConcurrentKafkaListenerContainerFactory<String, UserStatusChangedEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(userStatusChangedConsumerFactory());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		factory.setCommonErrorHandler(userStatusChangedErrorHandler);
		return factory;
	}
}
