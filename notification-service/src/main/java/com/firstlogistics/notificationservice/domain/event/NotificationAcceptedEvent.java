package com.firstlogistics.notificationservice.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record NotificationAcceptedEvent(
        @JsonProperty(required = false)
        UUID userId,
        UUID notificationId,
        String messageId,      // 슬랙 id
        String content,
        String messengerType
) {
    public static NotificationAcceptedEvent of(UUID userId, UUID notificationId, String messageId, String content, String messengerType) {
        return new NotificationAcceptedEvent(userId, notificationId, messageId, content, messengerType);
    }
}