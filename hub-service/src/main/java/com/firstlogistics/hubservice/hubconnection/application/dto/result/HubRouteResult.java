package com.firstlogistics.hubservice.hubconnection.application.dto.result;

import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRoute;

import java.util.List;
import java.util.UUID;

public record HubRouteResult(

    UUID sourceId,
    UUID destinationId,
    int count,
    int totalTime,
    int totalDistance,
    List<HubRouteLegResult> routes
) {
    public static HubRouteResult of(
            HubRoute hubRoute,
            HubRouteLegResult lastHubRouteLeg,
            List<HubRouteLegResult> routeLegResults
    ) {
        return new HubRouteResult(
                hubRoute.sourceHubId().id(),
                hubRoute.destinationHubId().id(),
                routeLegResults.size(),
                hubRoute.totalTime().minutes() + lastHubRouteLeg.minutes(),
                hubRoute.totalDistance().meters() + lastHubRouteLeg.meters(),
                routeLegResults
        );
    }
}
