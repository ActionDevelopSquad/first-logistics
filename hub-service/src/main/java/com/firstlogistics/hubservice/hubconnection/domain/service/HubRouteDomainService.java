package com.firstlogistics.hubservice.hubconnection.domain.service;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;

import java.util.UUID;

public interface HubRouteDomainService {
    HubRoute calculateRoute(HubId sourceHubId, HubId destinationHubId, UUID destinationCompanyId, RoutePolicy policy);
}
