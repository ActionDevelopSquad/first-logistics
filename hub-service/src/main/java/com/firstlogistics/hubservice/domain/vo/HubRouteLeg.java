package com.firstlogistics.hubservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class HubRouteLeg {
    private final HubId sourceHubId;
    private final HubId destinationHubId;
    private final Time time;
    private final Distance distance;

    private HubRouteLeg(HubId sourceHubId , HubId destinationHubId, Time time, Distance distance ){
        this.sourceHubId = sourceHubId;
        this.destinationHubId = destinationHubId;
        this.time = time;
        this.distance = distance;
    }
    public static  HubRouteLeg of(HubId sourceHubId , HubId destinationHubId, Time time, Distance distance ){
        return new HubRouteLeg(sourceHubId, destinationHubId, time,distance);
    }

}
