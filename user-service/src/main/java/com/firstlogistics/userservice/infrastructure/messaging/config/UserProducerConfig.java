package com.firstlogistics.userservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.kafka.ConsistentHashPartitioner;
import common.kafka.config.KafkaProducerConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserProducerConfig {

    private final KafkaProducerConfig kafkaProducerConfig;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    @Bean
    public ProducerFactory<String, Object> userProducerFactory() {
        Map<String, Object> props = kafkaProducerConfig.commonProducerProps();
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, ConsistentHashPartitioner.class);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(userProducerFactory());
    }
}