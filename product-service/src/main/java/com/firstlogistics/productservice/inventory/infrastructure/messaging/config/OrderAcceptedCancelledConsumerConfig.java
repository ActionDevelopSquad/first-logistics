package com.firstlogistics.productservice.inventory.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.productservice.inventory.infrastructure.messaging.event.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@RequiredArgsConstructor
public class OrderAcceptedCancelledConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<String, OrderCancelledEvent> orderAcceptedCancelledConsumerFactory() {
        JsonDeserializer<OrderCancelledEvent> deserializer = new JsonDeserializer<>(OrderCancelledEvent.class, objectMapper);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig.commonConsumerProps(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> orderAcceptedCancelledListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCancelledEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderAcceptedCancelledConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
