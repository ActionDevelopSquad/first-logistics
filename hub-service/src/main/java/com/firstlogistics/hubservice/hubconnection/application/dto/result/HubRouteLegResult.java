package com.firstlogistics.hubservice.hubconnection.application.dto.result;

import com.firstlogistics.hubservice.hubconnection.domain.vo.HubRouteLeg;

import java.util.UUID;

public record HubRouteLegResult(
        int sequence,
        UUID sourceId,
        UUID destinationId,
        int minutes,
        int meters

) {
    public static HubRouteLegResult of(int sequence, UUID sourceId, UUID destinationId,int minutes, int meters){
        return new HubRouteLegResult(sequence, sourceId, destinationId, minutes, meters);
    }
    public static HubRouteLegResult from(HubRouteLeg leg){
        return new HubRouteLegResult(
                leg.sequence(),
                leg.sourceId().id(),
                leg.destinationId().id(),
                leg.time().minutes(),
                leg.distance().meters()
        );
    }
}
