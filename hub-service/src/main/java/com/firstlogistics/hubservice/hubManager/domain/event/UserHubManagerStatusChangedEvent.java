package com.firstlogistics.hubservice.hubManager.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserHubManagerStatusChangedEvent(
        UUID userId,
        UUID organizationId,
        String userRole,
        String status
) {
    public boolean shouldCreateHubManager() {
        return "HUB_MANAGER".equals(userRole) && "APPROVED".equals(status);
    }

    public UUID hubId() {
        return organizationId;
    }
}
