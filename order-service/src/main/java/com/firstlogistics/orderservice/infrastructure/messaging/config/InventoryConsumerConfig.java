package com.firstlogistics.orderservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.orderservice.infrastructure.messaging.event.InventoryReservationFailedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.InventoryResultEvent;
import common.kafka.config.KafkaConsumerConfig;
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
public class InventoryConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ObjectMapper objectMapper;

    // ----- 재고 이벤트 성공 ----- //
    @Bean
    public ConsumerFactory<String, InventoryResultEvent> inventoryResultConsumerFactory() {
        return createFactory(InventoryResultEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryResultEvent> inventoryResultListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryResultEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(inventoryResultConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    // ----- 재고 예약 실패 ----- //
    @Bean
    public ConsumerFactory<String, InventoryReservationFailedEvent> inventoryReservationFailedConsumerFactory() {
        return createFactory(InventoryReservationFailedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservationFailedEvent> inventoryReservationFailedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryReservationFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(inventoryReservationFailedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    private <T> ConsumerFactory<String, T> createFactory(Class<T> targetClass) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetClass, objectMapper);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.commonConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }
}
