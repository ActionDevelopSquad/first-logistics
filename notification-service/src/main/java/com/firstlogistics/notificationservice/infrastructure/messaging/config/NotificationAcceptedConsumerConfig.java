package com.firstlogistics.notificationservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.notificationservice.domain.event.NotificationAcceptedEvent;
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
public class NotificationAcceptedConsumerConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final ObjectMapper objectMapper;

    /**
     * NotificationCreatedEvent를 처리하기 위한 ConsumerFactory 설정
     */
    @Bean
    public ConsumerFactory<String, NotificationAcceptedEvent> notificationCreatedConsumerFactory() {
        JsonDeserializer<NotificationAcceptedEvent> deserializer =
                new JsonDeserializer<>(NotificationAcceptedEvent.class, objectMapper);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                kafkaConsumerConfig.commonConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * 알림 발송 실패 시 재시도 정책 설정 (1초 간격, 최대 3회)
     */
    @Bean
    public DefaultErrorHandler notificationCreatedErrorHandler() {
        // 비즈니스 예외(예: 잘못된 Slack ID 등)는 재시도하지 않도록 추후 설정 가능
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
    }

    /**
     * KafkaListener가 사용할 ContainerFactory 설정
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationAcceptedEvent> notificationCreatedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationAcceptedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(notificationCreatedConsumerFactory());
        factory.setCommonErrorHandler(notificationCreatedErrorHandler());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}