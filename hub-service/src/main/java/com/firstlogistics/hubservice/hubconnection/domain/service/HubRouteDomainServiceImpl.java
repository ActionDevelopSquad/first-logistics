package com.firstlogistics.hubservice.hubconnection.domain.service;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import com.firstlogistics.hubservice.hubconnection.domain.strategy.HubRouteStrategy;
import com.firstlogistics.hubservice.hubconnection.domain.strategy.HubRouteStrategySelector;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HubRouteDomainServiceImpl implements HubRouteDomainService{
    private final HubRouteStrategySelector strategySelector;
    private final HubConnectionRepository hubConnectionRepository;
    private final HubRepository hubRepository;

    @Override
    public HubRoute calculateRoute(HubId sourceHubId, HubId destinationHubId, UUID destinationCompanyId, RoutePolicy policy) {
        List<HubConnection> hubConnections = hubConnectionRepository.findAll();
        Map<HubId, Hub> hubMap = hubRepository.findAll().stream()
                .collect(Collectors.toMap(Hub::getId, hub -> hub));

        HubRouteStrategy strategy = strategySelector.get(policy);
        return strategy.calculate(sourceHubId, destinationHubId, hubConnections, hubMap);
    }
}
