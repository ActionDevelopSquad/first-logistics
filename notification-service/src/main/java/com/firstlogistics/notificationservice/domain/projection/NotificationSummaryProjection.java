package com.firstlogistics.notificationservice.domain.projection;

import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSummaryProjection(
        UUID id,
        UUID receiverId,
        String content,
        NotificationType type,
        NotificationStatus status,
        LocalDateTime createdAt
) {}