package com.firstlogistics.hubservice.hubconnection.application.dto.command;

import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

public record UpdateHubConnectionCommand(
        Integer minutes,
        Integer meters
) {
    public UpdateHubConnectionCommand {
        boolean hasTime = minutes != null;
        boolean hasDistance = meters != null;

        if (!hasTime && !hasDistance) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_UPDATE_REQUEST);
        }

        if (minutes!= null &&minutes < 1) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_MINUTES);
        }
        if (meters != null && meters < 0) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_METERS);
        }
    }
}
