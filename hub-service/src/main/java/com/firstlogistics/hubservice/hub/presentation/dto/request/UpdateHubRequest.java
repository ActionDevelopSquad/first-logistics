package com.firstlogistics.hubservice.hub.presentation.dto.request;

import com.firstlogistics.hubservice.hub.application.dto.command.UpdateHubCommand;

public record UpdateHubRequest(
        String name,
        String roadAddress
) {
    public UpdateHubCommand toCommand(){
        return new UpdateHubCommand(
                name,
                roadAddress
        );
    }
}
