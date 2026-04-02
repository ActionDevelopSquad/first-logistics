package com.firstlogistics.hubservice.hub.application.dto.result;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSummaryDto;

import java.util.UUID;

public record HubSummaryResult(
        UUID hubId,
        String roadAddress,
        String name,
        HubStatus status
) {
    public static HubSummaryResult from(HubSummaryDto dto){
        return new HubSummaryResult(
          dto.hubId(),
          dto.roadAddress(),
          dto.name(),
          dto.status()
        );
    }
}
