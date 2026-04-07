package com.firstlogistics.productservice.inventory.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class InventoryProducerConfig {

    private final KafkaProducerConfig kafkaProducerConfig;
    private final ObjectMapper objectMapper;

    @Bean
    public ProducerFactory<String, Object> inventoryProducerFactory() {
        Map<String, Object> props = kafkaProducerConfig.commonProducerProps();
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, Object> inventoryKafkaTemplate() {
        return new KafkaTemplate<>(inventoryProducerFactory());
    }
}
