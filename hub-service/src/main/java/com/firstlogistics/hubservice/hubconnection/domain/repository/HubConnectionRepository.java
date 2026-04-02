package com.firstlogistics.hubservice.hubconnection.domain.repository;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;

public interface HubConnectionRepository {
    boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId);
    HubConnection save(HubConnection hubConnection);
}
