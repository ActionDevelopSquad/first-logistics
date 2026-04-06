package com.firstlogistics.notificationservice.domain.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDetailProjection(
        UUID notificationId,
        UUID userId,
        String messageId,
        String content,
        String messengerType,
        LocalDateTime createdAt
) {
}