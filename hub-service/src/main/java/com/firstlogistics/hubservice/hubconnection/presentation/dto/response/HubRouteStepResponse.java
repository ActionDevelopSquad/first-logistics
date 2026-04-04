package com.firstlogistics.hubservice.hubconnection.presentation.dto.response;

import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubRouteLegResult;

import java.util.UUID;

public record HubRouteStepResponse(
        int hubRouteSequence,
        UUID sourceId,
        UUID destinationId,
        int distanceMeters,
        int durationMinutes
) {
    public static HubRouteStepResponse from(HubRouteLegResult result){
        return new HubRouteStepResponse(
                result.sequence(),
                result.sourceId(),
                result.destinationId(),
                result.meters(),
                result.minutes()
        );
    }
}
