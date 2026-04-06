package com.firstlogistics.hubservice.hub.application.dto.command;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;

public record ChangeHubStatusCommand(
        HubStatus status
) {
    public ChangeHubStatusCommand{
        if(status == null)
            throw new HubException(HubErrorCode.INVALID_HUB_STATUS);
    }
    public ChangeHubStatusCommand(String status) {
        this(HubStatus.from(status));
    }
}
