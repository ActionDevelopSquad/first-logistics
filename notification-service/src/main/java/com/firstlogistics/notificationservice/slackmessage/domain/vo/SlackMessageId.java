package com.firstlogistics.notificationservice.slackmessage.domain.vo;

import java.util.UUID;

public record SlackMessageId(UUID id) {
    public static SlackMessageId of() {
        return SlackMessageId.of(UUID.randomUUID());
    }

    public static SlackMessageId of(UUID id) {
        return new SlackMessageId(id);
    }
}
