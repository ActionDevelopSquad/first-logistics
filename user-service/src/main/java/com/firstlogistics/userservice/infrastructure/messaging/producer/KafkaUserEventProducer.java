package com.firstlogistics.userservice.infrastructure.messaging.producer;

import com.firstlogistics.userservice.application.port.DlqAlert;
import com.firstlogistics.userservice.domain.event.UserStatusChangedDlqEvent;
import com.firstlogistics.userservice.domain.event.UserStatusChangedEvent;
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

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DlqAlert dlqAlert;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(UserStatusChangedEvent event) {
        String topic = switch (event.userRole()) {
            case HUB_MANAGER -> "user.hub.status.changed";
            case DELIVERY_MANAGER -> "user.delivery.status.changed";
            case COMPANY_MANAGER, MASTER -> null;
        };

        if (topic == null) {
            // MASTER, COMPANY_MANAGER는 발행하지 않음
            return;
        }

        kafkaTemplate.send(topic, event.userId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        publishDlq(event, ex, topic);
                        return;
                    }

                    log.info("Kafka 원본 토픽 발행 성공. topic={}, partition={}, offset={}, userId={}",
                            topic,
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