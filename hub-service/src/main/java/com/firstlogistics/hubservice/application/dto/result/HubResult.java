package com.firstlogistics.hubservice.application.dto.result;

import com.firstlogistics.hubservice.domain.entity.Hub;
import com.firstlogistics.hubservice.domain.enums.HubStatus;

import java.util.UUID;

public record HubResult(
        UUID HubId,
        String name,
        String roadAddress,
        double latitude,
        double longitude,
        HubStatus status
) {
    public static HubResult from(Hub hub){
        return new HubResult(
                hub.getId().id(),
                hub.getName(),
                hub.getAddress().roadAddress(),
                hub.getGeoLocation().latitude(),
                hub.getGeoLocation().longitude(),
                hub.getStatus()
        );
    }
}
