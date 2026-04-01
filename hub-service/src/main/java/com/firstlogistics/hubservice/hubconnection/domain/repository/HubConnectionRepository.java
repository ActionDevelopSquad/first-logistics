package com.firstlogistics.hubservice.hubconnection.domain.repository;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;

public interface HubConnectionRepository {
    boolean existsBySourceAndDestination(HubId sourceHubId, HubId destinationHubId);
}
