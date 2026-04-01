package com.firstlogistics.notificationservice.ailog.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record MessengerMessageId(UUID id) {
    public MessengerMessageId {
        Objects.requireNonNull(id, "MessengerMessageId must not be null");
    }
    public static MessengerMessageId of(UUID id) {
        return new MessengerMessageId(id);
    }
}