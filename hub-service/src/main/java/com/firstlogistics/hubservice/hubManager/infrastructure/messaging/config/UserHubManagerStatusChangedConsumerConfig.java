package com.firstlogistics.hubservice.hubManager.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstlogistics.hubservice.hubManager.domain.event.UserHubManagerStatusChangedEvent;
import com.firstlogistics.hubservice.hubManager.infrastructure.messaging.consumer.UserHubManagerStatusChangedRecoverer;
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
public class UserHubManagerStatusChangedConsumerConfig {
    private final ObjectMapper objectMapper;
    private final KafkaConsumerConfig kafkaConsumerConfig;

    public ConsumerFactory<String, UserHubManagerStatusChangedEvent> userHubManagerStatusChangedConsumerFactory(){
        JsonDeserializer<UserHubManagerStatusChangedEvent> deserializer =
                new JsonDeserializer<>(UserHubManagerStatusChangedEvent.class, objectMapper);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = kafkaConsumerConfig.commonConsumerProps();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "hub-service");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public DefaultErrorHandler userHubManagerStatusChangedErrorHandler(
            UserHubManagerStatusChangedRecoverer recoverer
    ){
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,UserHubManagerStatusChangedEvent> userHubManagerStatusChangedListenerContainerFactory(
            DefaultErrorHandler errorHandler
    ){
        ConcurrentKafkaListenerContainerFactory<String,UserHubManagerStatusChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(userHubManagerStatusChangedConsumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

}
