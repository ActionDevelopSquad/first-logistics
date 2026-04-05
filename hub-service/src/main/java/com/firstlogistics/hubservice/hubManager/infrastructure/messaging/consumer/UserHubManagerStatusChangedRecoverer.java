package com.firstlogistics.hubservice.hubManager.infrastructure.messaging.consumer;

import com.firstlogistics.hubservice.hubManager.domain.event.HubManagerAssignFailedEvent;
import com.firstlogistics.hubservice.hubManager.domain.event.UserHubManagerStatusChangedEvent;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.infrastructure.messaging.producer.HubManagerAssignFailedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserHubManagerStatusChangedRecoverer implements ConsumerRecordRecoverer {
    private final HubManagerAssignFailedProducer failedProducer;

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        Object value = consumerRecord.value();
        if (!(value instanceof UserHubManagerStatusChangedEvent event))
            return;

        if (e.getCause() instanceof HubManagerException he
                && he.getErrorCode() == HubManagerErrorCode.DUPLICATE_HUB_MANAGER) {
            log.info("허브 매니저 이미 존재. userId={}", event.userId());
            return;
        }

        log.error("허브 매니저 생성 최종 실패. userId={}, organizationId={}",
                event.userId(), event.organizationId(), e);

        failedProducer.publish(HubManagerAssignFailedEvent.from(event));
    }
}
