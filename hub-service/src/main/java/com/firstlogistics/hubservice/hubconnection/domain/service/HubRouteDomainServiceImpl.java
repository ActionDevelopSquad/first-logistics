package com.firstlogistics.hubservice.hubconnection.domain.service;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.strategy.HubRouteStrategy;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubRouteDomainServiceImpl implements HubRouteDomainService{
    private final HubRouteStrategy strategy;

    @Override
    public HubRoute calculateRoute(HubId sourceHubId, HubId destinationHubId, UUID destinationCompanyId, RoutePolicy policy) {
        return null;
    }
}
