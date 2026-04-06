package com.firstlogistics.aiservice.domain.event;

import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;

public record NotificationCreatedEvent(
        MessengerMessageId messageId,
        String slackId,
        String content,
        MessengerType messengerType
) {
    public static NotificationCreatedEvent of(MessengerMessageId messageId, String slackId, String content, MessengerType messengerType) {
        return new NotificationCreatedEvent(messageId, slackId, content, messengerType);
    }
}