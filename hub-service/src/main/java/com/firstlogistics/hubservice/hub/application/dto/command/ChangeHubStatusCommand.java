package com.firstlogistics.hubservice.hub.application.dto.command;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;

public record ChangeHubStatusCommand(
        HubStatus status
) {
    public ChangeHubStatusCommand(String status) {
        this(HubStatus.from(status));
    }
}
