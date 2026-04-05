package com.firstlogistics.notificationservice.domain.vo;

import java.util.UUID;

public record NotificationId(UUID id) {
    public static NotificationId of() {
        return NotificationId.of(UUID.randomUUID());
    }

    public static NotificationId of(UUID id) {
        return new NotificationId(id);
    }
}
