package com.firstlogistics.hubservice.hub.presentation.dto.response;

import com.firstlogistics.hubservice.hub.application.dto.result.HubDetailsResult;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubDetailResponse(
        UUID hubId,
        String name,
        String roadAddress,
        GeoLocationResponse geoLocation,
        HubStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static HubDetailResponse from(HubDetailsResult result){
        return new HubDetailResponse(
                result.hubId(),
                result.name(),
                result.roadAddress(),
                GeoLocationResponse.of(result.latitude(), result.longitude()),
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
