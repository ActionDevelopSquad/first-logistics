package com.firstlogistics.notificationservice.presentation.dto.response;

import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID receiverId,
        String content,
        NotificationType type,
        NotificationStatus status,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(NotificationSummaryResult result) {
        return new NotificationResponse(
                result.id(),
                result.receiverId(),
                result.content(),
                result.type(),
                result.status(),
                result.createdAt()
        );
    }
}