package com.firstlogistics.hubservice.hubManager.domain.event;

import java.util.UUID;

public record HubManagerAssignFailedEvent(
        UUID userId,
        UUID organizationId
) {
    public static HubManagerAssignFailedEvent from(UserHubManagerStatusChangedEvent event) {
        return new HubManagerAssignFailedEvent(
                event.userId(),
                event.organizationId()
        );
    }
}
