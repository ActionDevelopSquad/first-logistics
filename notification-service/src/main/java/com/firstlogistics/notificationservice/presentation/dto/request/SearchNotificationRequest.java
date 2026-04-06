package com.firstlogistics.notificationservice.presentation.dto.request;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;

import java.util.UUID;

public record SearchNotificationRequest(
        UUID receiverId,
        NotificationType type,
        NotificationStatus status,
        MessengerType messengerType
) {
    public NotificationSearchQuery toQuery() {
        return new NotificationSearchQuery(
                receiverId,
                type,
                status,
                messengerType
        );
    }
}