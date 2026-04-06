package com.firstlogistics.hubservice.hubconnection.domain.repository;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;

import java.util.List;

public interface HubConnectionRepository {
    boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId);
    HubConnection save(HubConnection hubConnection);
    List<HubConnection> findAll();
    HubConnection findById(HubConnectionId id);
    void delete(HubConnection hubConnection);
}
