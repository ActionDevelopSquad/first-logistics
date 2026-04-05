package com.firstlogistics.hubservice.hubconnection.domain.strategy;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;

import java.util.List;
import java.util.Map;

public class HubToHubStrategy implements  HubRouteStrategy{

    @Override
    public HubRoute calculate(HubId sourceHubId, HubId destinationHubId, List<HubConnection> connections, Map<HubId, Hub> hubMap) {
        return null;
    }
}
