package com.firstlogistics.aiservice.infrastructure.messaging.producer;

import com.firstlogistics.aiservice.domain.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventKafkaProducer {

    private static final String TOPIC_NOTIFICATION = "ai.notification.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        // key를 slackId로 설정하여 동일 사용자의 메시지 순서 보장
        kafkaTemplate.send(TOPIC_NOTIFICATION, event.slackId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("알림 이벤트 발행 성공 - topic: {}, messageId: {}",
                                TOPIC_NOTIFICATION, event.messageId());
                    } else {
                        log.error("알림 이벤트 발행 실패 - messageId: {}", event.messageId(), ex);
                    }
                });
    }
}