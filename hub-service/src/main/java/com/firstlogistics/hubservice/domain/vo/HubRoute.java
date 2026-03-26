package com.firstlogistics.hubservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class HubRoute {
    private final List<HubRouteLeg> routes;
    private final Time totalTime;
    private final Distance totalDistance;

    private HubRoute(List<HubRouteLeg> routes,Time time,Distance distance ){
        this.routes = routes;
        this.totalTime = time;
        this.totalDistance = distance;
    }
    public static  HubRoute of(List<HubRouteLeg> routes,Time time,Distance distance ){
        return new HubRoute(routes, time, distance);
    }

}
