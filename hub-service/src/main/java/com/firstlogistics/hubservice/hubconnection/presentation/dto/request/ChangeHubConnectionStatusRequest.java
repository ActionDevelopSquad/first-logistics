package com.firstlogistics.hubservice.hubconnection.presentation.dto.request;

import com.firstlogistics.hubservice.hubconnection.application.dto.command.ChangeHubConnectionStatusCommand;

public record ChangeHubConnectionStatusRequest(
        String status
) {
    public ChangeHubConnectionStatusCommand toCommand(){
        return new ChangeHubConnectionStatusCommand(
                status
        );
    }
}
