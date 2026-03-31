package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;

public record HubRouteLeg(
        HubId sourceHubId,
        HubId destinationHubId,
        Time time,
        Distance distance
) {
    public static  HubRouteLeg of(HubId sourceHubId , HubId destinationHubId, Time time, Distance distance ){
        return new HubRouteLeg(sourceHubId, destinationHubId, time,distance);
    }

}
