package com.firstlogistics.hubservice.hubconnection.infrastructure.cache;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;

import java.util.UUID;

public record HubConnectionCacheDto(
        UUID id,
        UUID sourceHubId,
        UUID destinationHubId,
        int minutes,
        int meters,
        HubConnectionStatus status
) {
    public static HubConnectionCacheDto from(HubConnection hubConnection) {
        return new HubConnectionCacheDto(
                hubConnection.getId().id(),
                hubConnection.getSourceHubId().id(),
                hubConnection.getDestinationHubId().id(),
                hubConnection.getTime().minutes(),
                hubConnection.getDistance().meters(),
                hubConnection.getStatus()
        );
    }

    public HubConnection toDomain() {
        return HubConnection.reconstitute(
                HubConnectionId.of(id),
                HubId.of(sourceHubId),
                HubId.of(destinationHubId),
                Time.of(minutes),
                Distance.of(meters),
                status
        );
    }
}
