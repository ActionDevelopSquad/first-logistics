package com.firstlogistics.hubservice.hubconnection.presentation.dto.response;

import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubRouteResult;

import java.util.List;
import java.util.UUID;

public record HubRouteResponse(
        UUID sourceId,
        UUID destinationId,
        int count,
        int totalTime,
        int totalDistance,
        List<HubRouteStepResponse> routes
) {
    public static HubRouteResponse from(HubRouteResult result){
        return new HubRouteResponse(
                result.sourceId(),
                result.destinationId(),
                result.count(),
                result.totalTime(),
                result.totalDistance(),
                result.routes().stream().map(HubRouteStepResponse::from).toList()
        );
    }
}
