package com.firstlogistics.aiservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.kafka.config.KafkaConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.firstlogistics.aiservice.domain.event.DeliveryAcceptedEvent;

import lombok.RequiredArgsConstructor;
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
public class DeliveryAcceptedConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<String, DeliveryAcceptedEvent> deliveryAcceptedConsumerFactory() {
        JsonDeserializer<DeliveryAcceptedEvent> deserializer = new JsonDeserializer<>(DeliveryAcceptedEvent.class, objectMapper);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.commonConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
//    public DefaultErrorHandler deliveryAcceptedErrorHandler(DeliveryAcceptedRecoverer recoverer) {
    public DefaultErrorHandler deliveryAcceptedErrorHandler() {

//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3L));

//        errorHandler.addNotRetryableExceptions(InvalidNotificationException.class);

//        return errorHandler;
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryAcceptedEvent> deliveryAcceptedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryAcceptedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(deliveryAcceptedConsumerFactory());
        factory.setCommonErrorHandler(deliveryAcceptedErrorHandler());

        // 수동 커밋 모드 (메시지 처리가 확실히 끝난 후 오프셋 저장)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
