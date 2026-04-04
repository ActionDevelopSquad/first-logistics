package com.firstlogistics.aiservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.annotations.Config;
import common.kafka.config.KafkaProducerConfig;
import lombok.RequiredArgsConstructor;
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
public class NotififcationProducerConfig {

    private final KafkaProducerConfig kafkaProducerConfig;
    private final ObjectMapper objectMapper;

    @Bean
    public ProducerFactory<String, Object> notificationProducerFactory() {
        Map<String, Object> props = kafkaProducerConfig.commonProducerProps();
        
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, Object> notificationKafkaTemplate(
            ProducerFactory<String, Object> notificationProducerFactory) {
        return new KafkaTemplate<>(notificationProducerFactory);
    }
}
