package com.firstlogistics.hubservice.domain.vo;

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
