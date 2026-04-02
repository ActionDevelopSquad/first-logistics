package com.firstlogistics.hubservice.hubconnection.application.dto.result;


import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;

import java.util.UUID;

public record HubConnectionResult(
        UUID sourceHubId,
        UUID destinationHubId,
        int minutes,
        int meters,
        HubConnectionStatus status
) {

    public static HubConnectionResult from(HubConnection hubConnection){
        return new HubConnectionResult(
                hubConnection.getSourceHubId().id(),
                hubConnection.getDestinationHubId().id(),
                hubConnection.getTime().minutes(),
                hubConnection.getDistance().meters(),
                hubConnection.getStatus()
        );
    }
}
