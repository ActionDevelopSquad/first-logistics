package com.firstlogistics.hubservice.hubconnection.domain.strategy;

import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

import java.util.EnumMap;
import java.util.Map;

public class HubRouteStrategySelector {
    private final Map<RoutePolicy, HubRouteStrategy> strategyMap;

    public HubRouteStrategySelector(
            P2PHubHybridStrategy p2PHubHybridStrategy,
            HubToHubStrategy hubToHubStrategy
    ){
        Map<RoutePolicy,HubRouteStrategy> map = new EnumMap<>(RoutePolicy.class);
        map.put(RoutePolicy.HYBRID, p2PHubHybridStrategy);
        //map.put(RoutePolicy.HUB_TO_HUB, hubToHubStrategy);
        this.strategyMap = Map.copyOf(map);
    }

    public HubRouteStrategy get(RoutePolicy policy){
        HubRouteStrategy strategy = strategyMap.get(policy);
        if(strategy == null)
           throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_ROUTE_POLICY);
        return strategy;
    }
}
