package com.firstlogistics.aiservice.domain.event;

import com.firstlogistics.aiservice.domain.enums.MessengerType;

import java.util.UUID;

public record NotificationCreatedEvent(
        UUID messageId,
        String slackId,
        String content,
        MessengerType messengerType
) {
    public static NotificationCreatedEvent of(UUID messageId, String slackId, String content, MessengerType messengerType) {
        return new NotificationCreatedEvent(messageId, slackId, content, messengerType);
    }
}