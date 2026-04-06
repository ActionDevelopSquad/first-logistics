package com.firstlogistics.orderservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.infrastructure.messaging.consumer.DeliveryCreatedRecoverer;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryCreatedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryCreationFailedEvent;
import com.firstlogistics.orderservice.infrastructure.messaging.event.DeliveryStatusChangedEvent;
import common.kafka.config.KafkaConsumerConfig;
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
public class DeliveryConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ObjectMapper objectMapper;

    // ----- 배송 생성 성공 ----- //
    @Bean
    public ConsumerFactory<String, DeliveryCreatedEvent> deliveryCreatedConsumerFactory() {
        return createFactory(DeliveryCreatedEvent.class);
    }

    @Bean
    public DefaultErrorHandler deliveryCreatedErrorHandler(DeliveryCreatedRecoverer recoverer) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));

        errorHandler.addNotRetryableExceptions(
                OrderException.class,
                IllegalArgumentException.class
        );

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryCreatedEvent> deliveryCreatedListenerContainerFactory(
            DefaultErrorHandler deliveryCreatedErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(deliveryCreatedConsumerFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        factory.setCommonErrorHandler(deliveryCreatedErrorHandler);

        return factory;
    }

    // ----- 배송 생성 실패 ----- //
    @Bean
    public ConsumerFactory<String, DeliveryCreationFailedEvent> deliveryFailedConsumerFactory() {
        return createFactory(DeliveryCreationFailedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryCreationFailedEvent> deliveryFailedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryCreationFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(deliveryFailedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }


    // ----- 배송 상태 변경 ----- //
    @Bean
    public ConsumerFactory<String, DeliveryStatusChangedEvent> deliveryStatusChangedConsumerFactory() {
        return createFactory(DeliveryStatusChangedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryStatusChangedEvent> deliveryStatusChangedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryStatusChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(deliveryStatusChangedConsumerFactory());
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