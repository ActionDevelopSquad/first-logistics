package com.firstlogistics.userservice.infrastructure.messaging.consumer;

import com.firstlogistics.userservice.application.service.UserCompensationService;
import com.firstlogistics.userservice.domain.event.UserAssignFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAssignFailedConsumer {

    private final UserCompensationService userCompensationService;

    @KafkaListener(
            topics = {"hub.manager.assign.failed",
                      "delivery.manager.assign.failed" },
            groupId = "user-assign-failed-handler",
            containerFactory = "userAssignListenerContainerFactory"
    )
    public void consume(UserAssignFailedEvent event, Acknowledgment ack) {
        log.error("담당자 생성 실패 이벤트 수신. userId={}, organizationId={}", event.userId(), event.organizationId());

        try {
            userCompensationService.rollbackUserAssign(event);
            log.warn("담당자 생성 실패 처리로 사용자 상태를 보상 처리했습니다. userId={}", event.userId());

            ack.acknowledge();
        } catch (Exception ex) {
            log.error("담당자 생성 실패 보상 처리에 실패했습니다. 수동 처리 필요. userId={}", event.userId(), ex);
        }
    }
}