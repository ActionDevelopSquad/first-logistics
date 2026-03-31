package com.firstlogistics.deliverservice.infrastructure.messaging.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DeliveryProducerConfig {

	private final KafkaProducerConfig kafkaProducerConfig;

	@Bean
	public ProducerFactory<String, Object> deliveryProducerFactory() {
		Map<String, Object> props = kafkaProducerConfig.commonProducerProps();
		props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
			ConsistentHashPartitioner.class);
		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	public KafkaTemplate<String, Object> deliveryKafkaTemplate() {
		return new KafkaTemplate<>(deliveryProducerFactory());
	}
}
