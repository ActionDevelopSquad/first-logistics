package com.firstlogistics.notificationservice.application.service;

import com.firstlogistics.notificationservice.application.dto.command.CreateNotificationCommand;
import com.firstlogistics.notificationservice.application.dto.result.CreateNotificationResult;
import com.firstlogistics.notificationservice.domain.client.NotificationClient;
import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import com.firstlogistics.notificationservice.domain.vo.NotificationId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationClient notificationClient;

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

        try {
            notificationClient.send(savedNotification.getMessageId(), savedNotification.getContent());

            savedNotification.updateStatus(NotificationStatus.SENT);
        } catch (NotificationException e) {
            throw e;
        } catch (Exception e) {
            log.error("슬랙 전송 실패: {}", e.getMessage());
            savedNotification.updateStatus(NotificationStatus.FAILED);

            throw new NotificationException(NotificationErrorCode.SLACK_SEND_FAILED);
        }

        return CreateNotificationResult.from(savedNotification);
    }

    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(NotificationId.of(notificationId))
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notificationRepository.delete(notification, userId);
    }
}
