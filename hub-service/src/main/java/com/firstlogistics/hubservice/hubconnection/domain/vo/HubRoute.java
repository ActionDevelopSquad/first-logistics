package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;

import java.util.List;

public record HubRoute(
        List<HubRouteLeg> routes,
        HubId sourceId,
        HubId destinationId,
        Time totalTime,
        Distance totalDistance,
        int routeCount
) {

    public static  HubRoute of(List<HubRouteLeg> routes, HubId sourceHubId, HubId destinationHubId, Time time, Distance distance, int routeCount ){
        return new HubRoute(routes,sourceHubId,destinationHubId, time, distance,routeCount);
    }

}
