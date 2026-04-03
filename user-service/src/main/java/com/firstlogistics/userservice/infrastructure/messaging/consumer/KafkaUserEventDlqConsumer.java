package com.firstlogistics.userservice.infrastructure.messaging.consumer;

import com.firstlogistics.userservice.application.port.DlqAlert;
import com.firstlogistics.userservice.domain.event.UserStatusChangedDlqEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventDlqConsumer {

    private final DlqAlert dlqAlert;

    @KafkaListener(
            topics = {
                    "user.hub.status.changed.dlq",
                    "user.delivery.status.changed.dlq"
            },
            groupId = "user-status-dlq-handler",
            containerFactory = "userStatusListenerContainerFactory"
    )
    public void consume(UserStatusChangedDlqEvent event, Acknowledgment ack) {
        try {
            log.error("DLQ 메시지 수신 userId={}, failedAt={}", event.userId(), event.failedAt());
            dlqAlert.alertStatus(event);

        } catch (Exception ex) {
            log.error("DLQ 알림 처리 실패 userId={}, cause={}", event.userId(), ex.toString());

        } finally {
            ack.acknowledge();
        }
    }
}