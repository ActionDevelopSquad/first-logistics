package com.firstlogistics.notificationservice.application.service;

import com.firstlogistics.notificationservice.application.dto.command.CreateNotificationCommand;
import com.firstlogistics.notificationservice.application.dto.result.CreateNotificationResult;
import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public CreateNotificationResult createDeadlineNotification(CreateNotificationCommand command) {

        Notification notification = Notification.create(
                command.notificationId(),
                command.userId(),
                command.messageId(),
                command.content(),
                NotificationType.DEADLINE,
                command.messengerType()
        );

        Notification savedNotification = notificationRepository.save(notification);

        return CreateNotificationResult.from(savedNotification);
    }
}
