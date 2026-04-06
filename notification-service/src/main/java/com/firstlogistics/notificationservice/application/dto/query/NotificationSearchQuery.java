package com.firstlogistics.notificationservice.application.dto.query;

import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;

import java.util.UUID;

public record NotificationSearchQuery(
        UUID receiverId,
        NotificationType type,
        NotificationStatus status,
        MessengerType messengerType
) {}