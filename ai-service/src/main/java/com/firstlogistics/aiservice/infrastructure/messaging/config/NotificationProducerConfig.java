package com.firstlogistics.aiservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.aiservice.domain.event.NotificationCreatedEvent;
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
public class NotificationProducerConfig {

    private final KafkaProducerConfig kafkaProducerConfig;
    private final ObjectMapper objectMapper;

    @Bean
    public ProducerFactory<String, NotificationCreatedEvent> notificationProducerFactory() {
        Map<String, Object> props = kafkaProducerConfig.commonProducerProps();
        
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, NotificationCreatedEvent> notificationKafkaTemplate(
            ProducerFactory<String, NotificationCreatedEvent> notificationProducerFactory) {
        return new KafkaTemplate<>(notificationProducerFactory);
    }
}
