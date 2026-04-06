package com.firstlogistics.hubservice.hub.application.dto.command;

import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;

public record UpdateHubCommand(
        String name,
        String roadAddress
) {
    public UpdateHubCommand {
        boolean hasName = name != null && !name.isBlank();
        boolean hasAddress = roadAddress != null && !roadAddress.isBlank();

        if (!hasName && !hasAddress) {
            throw new HubException(HubErrorCode.INVALID_HUB_UPDATE_REQUEST);
        }

        if (name != null && name.isBlank()) {
            throw new HubException(HubErrorCode.INVALID_HUB_NAME);
        }
        if (roadAddress != null && roadAddress.isBlank()) {
            throw new HubException(HubErrorCode.INVALID_HUB_ADDRESS);
        }
    }
}
