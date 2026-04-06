package com.firstlogistics.hubservice.hubconnection.application.dto.command;


import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

public record ChangeHubConnectionStatusCommand(
        HubConnectionStatus status
) {
    public ChangeHubConnectionStatusCommand{
        if(status == null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_STATUS);
    }
    public ChangeHubConnectionStatusCommand(String status) {
        this(HubConnectionStatus.from(status));
    }
}
