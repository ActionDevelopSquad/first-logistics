package com.firstlogistics.notificationservice.application.dto.command;

import java.util.UUID;

public record CreateNotificationCommand (
        UUID userId,
        UUID notificationId,
        String messageId,
        String content,
        String messengerType
) {
}