package com.firstlogistics.hubservice.hubconnection.presentation.dto.request;

import com.firstlogistics.hubservice.hubconnection.application.dto.command.UpdateHubConnectionCommand;

public record UpdateHubConnectionRequest(
        Integer minutes,
        Integer meters
) {
    public UpdateHubConnectionCommand toCommand(){
        return new UpdateHubConnectionCommand(
                minutes,
                meters
        );
    }
}
