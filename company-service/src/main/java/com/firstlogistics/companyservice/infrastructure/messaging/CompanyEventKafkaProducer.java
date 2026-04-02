package com.firstlogistics.companyservice.infrastructure.messaging;

import com.firstlogistics.companyservice.domain.event.CompanyActivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeactivatedEvent;
import com.firstlogistics.companyservice.domain.event.CompanyDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyEventKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CompanyCreatedEvent event) {
        log.info("[Kafka] 업체 등록 이벤트 발행 - companyId: {}", event.companyId());
        kafkaTemplate.send("company.registered", event.companyId().toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CompanyActivatedEvent event) {
        log.info("[Kafka] 업체 활성화 이벤트 발행 - companyId: {}", event.companyId());
        kafkaTemplate.send("company.activated", event.companyId().toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CompanyDeactivatedEvent event) {
        log.info("[Kafka] 업체 비활성화 이벤트 발행 - companyId: {}", event.companyId());
        kafkaTemplate.send("company.deactivated", event.companyId().toString(), event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CompanyDeletedEvent event) {
        log.info("[Kafka] 업체 삭제 이벤트 발행 - companyId: {}", event.companyId());
        kafkaTemplate.send("company.deleted", event.companyId().toString(), event);
    }
}
