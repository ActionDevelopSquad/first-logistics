package com.firstlogistics.hubservice.hubManager.application.dto.command;

import com.firstlogistics.hubservice.hubManager.domain.event.UserHubManagerStatusChangedEvent;

import java.util.UUID;

public record CreateHubManagerCommand(
        UUID userId,
        UUID hubId
) {
    public static CreateHubManagerCommand from(UserHubManagerStatusChangedEvent event) {
        return new CreateHubManagerCommand(
                event.userId(),
                event.hubId()
        );
    }
}
