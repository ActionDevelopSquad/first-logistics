package com.firstlogistics.userservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.userservice.domain.event.DeliveryManagerAssignFailedEvent;
import common.kafka.config.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserDeliveryManagerConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    @Bean
    public ConsumerFactory<String, DeliveryManagerAssignFailedEvent> deliveryManagerConsumerFactory() {
        JsonDeserializer<DeliveryManagerAssignFailedEvent> deserializer = new JsonDeserializer<>(DeliveryManagerAssignFailedEvent.class, objectMapper);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = kafkaConsumerConfig.commonConsumerProps();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public DefaultErrorHandler deliveryManagerErrorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryManagerAssignFailedEvent> deliveryManagerListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryManagerAssignFailedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(deliveryManagerConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(deliveryManagerErrorHandler());

        return factory;
    }
}