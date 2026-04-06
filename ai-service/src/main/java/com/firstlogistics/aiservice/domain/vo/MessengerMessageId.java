package com.firstlogistics.aiservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record MessengerMessageId(UUID id) {
    public static MessengerMessageId of() {
        return MessengerMessageId.of(UUID.randomUUID());
    }
    public static MessengerMessageId of(UUID id) {
        return new MessengerMessageId(id);
    }
}