package com.firstlogistics.companyservice.infrastructure.messaging;

import com.firstlogistics.companyservice.application.port.CompanyEventPublisher;
import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyEventKafkaProducer implements CompanyEventPublisher {

    private static final String TOPIC = "company.registered";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public void publish(CompanyCreatedEvent event) {
        springEventPublisher.publishEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CompanyCreatedEvent event) {
        log.info("[Kafka] 업체 등록 이벤트 발행 - companyId: {}", event.companyId());
        kafkaTemplate.send(TOPIC, event.companyId().toString(), event);
    }
}