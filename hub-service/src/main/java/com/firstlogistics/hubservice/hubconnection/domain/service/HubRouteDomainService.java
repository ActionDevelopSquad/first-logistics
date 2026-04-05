package com.firstlogistics.hubservice.hubconnection.domain.service;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;


public interface HubRouteDomainService {
    HubRoute calculateRoute(HubId sourceHubId, HubId destinationHubId, RoutePolicy policy);
}
