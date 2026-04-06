package com.firstlogistics.notificationservice.application.dto.result;

import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.projection.NotificationSummaryProjection;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSummaryResult(
        UUID id,
        UUID receiverId,
        String content,
        NotificationType type,
        NotificationStatus status,
        LocalDateTime createdAt
) {
    public static NotificationSummaryResult from(NotificationSummaryProjection dto) {
        return new NotificationSummaryResult(
                dto.id(),
                dto.receiverId(),
                dto.content(),
                dto.type(),
                dto.status(),
                dto.createdAt()
        );
    }
}