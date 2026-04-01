package com.firstlogistics.hubservice.hub.presentation.dto.response;

import com.firstlogistics.hubservice.hub.application.dto.result.HubResult;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;

import java.util.UUID;

public record HubResponse(
        UUID hubId,
        String name,
        String roadAddress,
        GeoLocationResponse geoLocation,
        HubStatus status
) {
    public static HubResponse from(HubResult result){
        return new HubResponse(
                result.hubId(),
                result.name(),
                result.roadAddress(),
                GeoLocationResponse.of(result.latitude(), result.longitude()),
                result.status()
        );
    }


}
