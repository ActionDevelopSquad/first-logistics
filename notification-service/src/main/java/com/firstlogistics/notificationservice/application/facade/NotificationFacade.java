package com.firstlogistics.notificationservice.application.facade;

import com.firstlogistics.notificationservice.application.dto.command.CreateNotificationCommand;
import com.firstlogistics.notificationservice.application.service.NotificationCommandService;
import com.firstlogistics.notificationservice.domain.event.NotificationAcceptedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationCommandService notificationService;

    @Transactional
    public void handleDeadlineNotificationRequest(NotificationAcceptedEvent event) {
        // 1. Event -> Command 변환
        CreateNotificationCommand command = new CreateNotificationCommand(
                event.userId(),
                event.notificationId(),
                event.messageId(),
                event.content(),
                event.messengerType()
        );

        notificationService.createDeadlineNotification(command);

    }
}