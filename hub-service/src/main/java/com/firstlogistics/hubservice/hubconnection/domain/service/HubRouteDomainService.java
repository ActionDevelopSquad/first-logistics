package com.firstlogistics.hubservice.hubconnection.domain.service;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;

import java.util.List;
import java.util.Map;


public interface HubRouteDomainService {
    HubRoute calculateRoute(HubId sourceHubId, HubId destinationHubId, RoutePolicy policy, Map<HubId, Hub> hubMap, List<HubConnection> connections);
}
