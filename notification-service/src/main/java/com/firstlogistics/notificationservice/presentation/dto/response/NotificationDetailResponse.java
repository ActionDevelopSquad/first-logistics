package com.firstlogistics.notificationservice.presentation.dto.response;

import com.firstlogistics.notificationservice.application.dto.result.NotificationDetailResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDetailResponse(
        UUID id,
        UUID receiverId,
        String messageId,
        String content,
        String type,
        String status,
        String messengerType,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static NotificationDetailResponse from(NotificationDetailResult result) {
        return new NotificationDetailResponse(
                result.id(),
                result.receiverId(),
                result.messageId(),
                result.content(),
                result.type().name(),
                result.status().name(),
                result.messengerType().name(),
                result.readAt(),
                result.createdAt()
        );
    }
}