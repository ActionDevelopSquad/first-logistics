package com.firstlogistics.orderservice.infrastructure.messaging.config;

import common.kafka.ConsistentHashPartitioner;
import common.kafka.config.KafkaProducerConfig;
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
public class OrderProducerConfig {

    private final KafkaProducerConfig kafkaProducerConfig;

    @Bean
    public ProducerFactory<String, Object> orderProducerFactory() {
        Map<String, Object> props = kafkaProducerConfig.commonProducerProps();
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
                ConsistentHashPartitioner.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> orderKafkaTemplate(
            ProducerFactory<String, Object> orderProducerFactory) {
        return new KafkaTemplate<>(orderProducerFactory);
    }
}
