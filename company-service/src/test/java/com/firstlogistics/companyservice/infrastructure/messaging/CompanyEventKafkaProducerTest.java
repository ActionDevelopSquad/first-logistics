package com.firstlogistics.companyservice.infrastructure.messaging;

import com.firstlogistics.companyservice.domain.event.CompanyActivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeactivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyEventKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CompanyEventKafkaProducer companyEventKafkaProducer;

    @Test
    @DisplayName("CompanyCreatedEvent를 Kafka 토픽으로 발행한다")
    void handle_companyCreated_sendsToKafka() {
        // given
        UUID companyId = UUID.randomUUID();
        CompanyCreatedEvent event = new CompanyCreatedEvent(companyId, "테스트업체");

        // when
        companyEventKafkaProducer.handle(event);

        // then
        verify(kafkaTemplate).send("company.registered", companyId.toString(), event);
    }

    @Test
    @DisplayName("CompanyActivatedEvent를 Kafka 토픽으로 발행한다")
    void handle_companyActivated_sendsToKafka() {
        // given
        UUID companyId = UUID.randomUUID();
        CompanyActivatedEvent event = new CompanyActivatedEvent(companyId);

        // when
        companyEventKafkaProducer.handle(event);

        // then
        verify(kafkaTemplate).send("company.activated", companyId.toString(), event);
    }

    @Test
    @DisplayName("CompanyDeactivatedEvent를 Kafka 토픽으로 발행한다")
    void handle_companyDeactivated_sendsToKafka() {
        // given
        UUID companyId = UUID.randomUUID();
        CompanyDeactivatedEvent event = new CompanyDeactivatedEvent(companyId);

        // when
        companyEventKafkaProducer.handle(event);

        // then
        verify(kafkaTemplate).send("company.deactivated", companyId.toString(), event);
    }

    @Test
    @DisplayName("CompanyDeletedEvent를 Kafka 토픽으로 발행한다")
    void handle_companyDeleted_sendsToKafka() {
        // given
        UUID companyId = UUID.randomUUID();
        CompanyDeletedEvent event = new CompanyDeletedEvent(companyId);

        // when
        companyEventKafkaProducer.handle(event);

        // then
        verify(kafkaTemplate).send("company.deleted", companyId.toString(), event);
    }
}
