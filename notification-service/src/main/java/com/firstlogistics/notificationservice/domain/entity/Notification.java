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
    private NotificationId id;
    private Long version;
    private UUID receiverId;
    private String messageId;
    private String content;
    private NotificationType type;
    private NotificationStatus status;
    private MessengerType messengerType;
    private LocalDateTime readAt;

    public static Notification create(
            UUID notificationId,
            UUID receiverId,
            String messageId,
            String content,
            NotificationType type,
            String messengerType
    ) {
        return new Notification(
                NotificationId.of(notificationId),
                null,
                receiverId,
                messageId,
                content,
                type,
                NotificationStatus.PENDING,
                MessengerType.valueOf(messengerType.toUpperCase()),
                null
        );
    }

    public static Notification reconstitute(
            NotificationId id,
            Long version,
            UUID receiverId,
            String messageId,
            String content,
            NotificationType type,
            NotificationStatus status,
            MessengerType messengerType,
            LocalDateTime readAt
    ) {
        return new Notification(
                id,
                version,
                receiverId,
                messageId,
                content,
                type,
                status,
                messengerType,
                readAt
        );
    }

    public void updateStatus(NotificationStatus status) {
        if (!this.status.canTransitionTo(status)) {
                throw new NotificationException(NotificationErrorCode.CANNOT_UPDATE_STATUS);
        }
        this.status = status;

    }

}
