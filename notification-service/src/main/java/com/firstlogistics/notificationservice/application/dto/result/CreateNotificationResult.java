package com.firstlogistics.notificationservice.application.dto.result;

import com.firstlogistics.notificationservice.domain.entity.Notification;

import java.util.UUID;

public record CreateNotificationResult(
        UUID notificationId,
        UUID receiverId,
        String status,
        String type
) {
    public static CreateNotificationResult from(Notification notification) {
        return new CreateNotificationResult(
                notification.getId().id(),
                notification.getReceiverId(),
                notification.getStatus().name(),
                notification.getType().name()
        );
    }
}