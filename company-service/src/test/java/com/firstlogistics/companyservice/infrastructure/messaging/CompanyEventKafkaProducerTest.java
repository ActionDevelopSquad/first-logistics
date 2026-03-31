package com.firstlogistics.companyservice.infrastructure.messaging;

import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyEventKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ApplicationEventPublisher springEventPublisher;

    @InjectMocks
    private CompanyEventKafkaProducer companyEventKafkaProducer;

    @Test
    @DisplayName("publish 호출 시 Spring 내부 이벤트로 등록한다")
    void publish_delegatesToSpringEventPublisher() {
        // given
        CompanyCreatedEvent event = new CompanyCreatedEvent(UUID.randomUUID(), "테스트업체");

        // when
        companyEventKafkaProducer.publish(event);

        // then
        verify(springEventPublisher).publishEvent(event);
    }

    @Test
    @DisplayName("handle 호출 시 Kafka 토픽으로 이벤트를 발행한다")
    void handle_sendsToKafkaTopic() {
        // given
        UUID companyId = UUID.randomUUID();
        CompanyCreatedEvent event = new CompanyCreatedEvent(companyId, "테스트업체");

        // when
        companyEventKafkaProducer.handle(event);

        // then
        verify(kafkaTemplate).send("company.registered", companyId.toString(), event);
    }
}