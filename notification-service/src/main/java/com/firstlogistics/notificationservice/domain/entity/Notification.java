package com.firstlogistics.notificationservice.domain.entity;

import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.vo.NotificationId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification {
    NotificationId id;
    UUID receiverId;
    String slackId;
    String content;
    NotificationType type;
    NotificationStatus status;
    MessengerType messengerType;
    LocalDateTime readAt;

    public static Notification create(
            UUID receiverId,
            String slackId,
            String content,
            NotificationType type,
            MessengerType messengerType
    ) {
        return new Notification(
                NotificationId.of(),
                receiverId,
                slackId,
                content,
                type,
                NotificationStatus.PENDING,
                MessengerType.SLACK,
                null
        );
    }

    public void updateStatus(NotificationStatus status) {
        if (!this.status.canTransitionTo(status)) {
                throw new NotificationException(NotificationErrorCode.CANNOT_UPDATE_STATUS);
        }
        this.status = status;

    }
}
