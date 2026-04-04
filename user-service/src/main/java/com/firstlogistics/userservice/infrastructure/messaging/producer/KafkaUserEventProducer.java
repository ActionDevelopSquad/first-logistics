package com.firstlogistics.userservice.infrastructure.messaging.producer;

import com.firstlogistics.userservice.application.port.DlqAlert;
import com.firstlogistics.userservice.domain.event.UserStatusChangedDlqEvent;
import com.firstlogistics.userservice.domain.event.UserStatusChangedEvent;
import common.security.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaUserEventProducer {

    private static final String USER_STATUS_CHANGED_TOPIC = "user.status.changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DlqAlert dlqAlert;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(UserStatusChangedEvent event) {
        if (event.userRole() != UserRole.HUB_MANAGER &&
                event.userRole() != UserRole.DELIVERY_MANAGER) {
            return;
        }

        kafkaTemplate.send(USER_STATUS_CHANGED_TOPIC, event.userId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        publishDlq(event, ex, USER_STATUS_CHANGED_TOPIC);
                        return;
                    }

                    log.info("Kafka 원본 토픽 발행 성공. topic={}, partition={}, offset={}, userId={}",
                            USER_STATUS_CHANGED_TOPIC,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.userId());
                });
    }

    private void publishDlq(UserStatusChangedEvent event, Throwable ex, String topic) {
        UserStatusChangedDlqEvent dlqEvent = UserStatusChangedDlqEvent.from(event, topic, ex);
        kafkaTemplate.send(topic + ".dlq", event.userId().toString(), dlqEvent)
                .whenComplete((dlqResult, dlqEx) -> {
                    if (dlqEx != null) {
                        log.error("DLQ 발행이 실패했습니다. 수동 처리가 필요합니다. DLQ_Topic={}, userId={}", topic + ".dlq", event.userId(), dlqEx);
                        dlqAlert.alertStatus(dlqEvent);

                        return;
                    }

                    log.warn("DLQ 발행 성공. DLQ_Topic={}, partition={}, offset={}, userId={}",
                            topic + ".dlq",
                            dlqResult.getRecordMetadata().partition(),
                            dlqResult.getRecordMetadata().offset(),
                            event.userId());
                });
    }
}