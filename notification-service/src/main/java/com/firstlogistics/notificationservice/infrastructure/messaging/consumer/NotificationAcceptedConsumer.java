package com.firstlogistics.notificationservice.infrastructure.messaging.consumer;

import com.firstlogistics.notificationservice.application.facade.NotificationFacade;
import com.firstlogistics.notificationservice.domain.event.NotificationAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationAcceptedConsumer {

    private final NotificationFacade notificationFacade;

    @KafkaListener(
            topics = "ai.notification.created",
            groupId = "notification-service-group",
            containerFactory = "notificationCreatedListenerContainerFactory"
    )
    public void consume(NotificationAcceptedEvent event) {
        // record의 accessor를 사용하여 userId 포함 여부 확인 로그
        log.info("Received notification request. User: {}, MsgId: {}", event.userId(), event.messageId());

        notificationFacade.handleDeadlineNotificationRequest(event);
    }
}