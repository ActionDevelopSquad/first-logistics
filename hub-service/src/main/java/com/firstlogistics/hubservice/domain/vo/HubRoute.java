package com.firstlogistics.hubservice.domain.vo;

import java.util.List;

public record HubRoute(
        List<HubRouteLeg> routes,
        Time totalTime,
        Distance totalDistance
) {

    public static  HubRoute of(List<HubRouteLeg> routes,Time time,Distance distance ){
        return new HubRoute(routes, time, distance);
    }

}
