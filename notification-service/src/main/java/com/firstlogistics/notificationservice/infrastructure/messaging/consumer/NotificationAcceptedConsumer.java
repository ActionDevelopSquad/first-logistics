package com.firstlogistics.notificationservice.infrastructure.messaging.consumer;

import com.firstlogistics.notificationservice.application.facade.NotificationFacade;
import com.firstlogistics.notificationservice.domain.event.NotificationAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

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
    public void consume(NotificationAcceptedEvent event, Acknowledgment ack) {
        try {
            log.info("Received notification request. User: {}, MsgId: {}", event.userId(), event.messageId());

            notificationFacade.handleDeadlineNotificationRequest(event);

            ack.acknowledge();
            log.info("Message acknowledged successfully.");

        } catch (Exception e) {
            log.error("Error processing notification: {}", e.getMessage());
        }
    }
}