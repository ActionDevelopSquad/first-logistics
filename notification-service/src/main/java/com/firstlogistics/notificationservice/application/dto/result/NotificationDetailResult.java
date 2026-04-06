package com.firstlogistics.notificationservice.application.dto.result;

import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.projection.NotificationDetailProjection;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDetailResult(
        UUID id,
        UUID receiverId,
        String messageId,
        String content,
        NotificationType type,
        NotificationStatus status,
        MessengerType messengerType,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static NotificationDetailResult from(NotificationDetailProjection projection) {
        return new NotificationDetailResult(
                projection.id(),
                projection.receiverId(),
                projection.messageId(),
                projection.content(),
                projection.type(),
                projection.status(),
                projection.messengerType(),
                projection.readAt(),
                projection.createdAt()
        );
    }
}