package com.firstlogistics.hubservice.hubconnection.presentation.dto.response;

import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;

import java.util.UUID;

public record HubConnectionResponse(
        UUID sourceHubId,
        UUID destinationHubId,
        int minutes,
        int meters,
        String status
) {
    public static HubConnectionResponse from(HubConnectionResult result){
        return new HubConnectionResponse(
                result.sourceHubId(),
                result.destinationHubId(),
                result.minutes(),
                result.meters(),
                result.status().name()
        );
    }
}
