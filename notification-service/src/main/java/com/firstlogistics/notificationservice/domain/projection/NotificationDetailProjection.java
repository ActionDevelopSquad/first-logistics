package com.firstlogistics.notificationservice.domain.projection;

import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDetailProjection(
        UUID id,
        UUID receiverId,
        String messageId,
        String content,
        NotificationType type,
        NotificationStatus status,
        MessengerType messengerType,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {}