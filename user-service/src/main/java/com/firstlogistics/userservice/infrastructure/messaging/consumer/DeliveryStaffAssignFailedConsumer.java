package com.firstlogistics.userservice.infrastructure.messaging.consumer;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.event.DeliveryStaffAssignFailedEvent;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryStaffAssignFailedConsumer {

    private final UserRepository userRepository;

    @KafkaListener(
            topics = "delivery.staff.assign.failed",
            groupId = "user-service-delivery-failed-handler"
    )
    public void consume(DeliveryStaffAssignFailedEvent event, Acknowledgment ack) {
        log.error("배송 담당자 생성 실패 이벤트 수신. userId={}, organizationId={}, username={}",
                event.userId(),
                event.organizationId(),
                event.username()
        );

        User user = userRepository.findByIdNotDeleted(event.userId());
        try {
            user.rollbackStatus();
            userRepository.update(user);
        } catch (Exception ex) {
            log.warn("사용자 상태 수정에 실패했습니다. 수동 처리 필요. userId={}, newStatus={}", event.userId(), user.getStatus());
        }

        log.warn("배송 서비스 처리 실패로 사용자 상태를 보상 처리했습니다. userId={}, newStatus={}", event.userId(), user.getStatus());

        ack.acknowledge();
    }
}