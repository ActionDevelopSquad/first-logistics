package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;

public record HubRouteLeg(
        int sequence,
        HubId sourceHubId,
        HubId destinationHubId,
        Time time,
        Distance distance
) {
    public static  HubRouteLeg of(int sequence,HubId sourceHubId , HubId destinationHubId, Time time, Distance distance ){
        return new HubRouteLeg(sequence,sourceHubId, destinationHubId, time,distance);
    }

}
