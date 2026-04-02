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
            groupId = "user-status-dlq-handler"
    )
    public void consume(UserStatusChangedDlqEvent event, Acknowledgment ack) {
        log.error("DLQ 메시지 수신 userId={}, originalTopic={}, role={}, " +
                        "previousStatus={}, currentStatus={}, errorMessage={}, failedAt={}",
                event.userId(),
                event.originalTopic(),
                event.userRole(),
                event.previousStatus(),
                event.currentStatus(),
                event.errorMessage(),
                event.failedAt()
        );

        dlqAlert.alertStatus(event);
        ack.acknowledge();
    }
}