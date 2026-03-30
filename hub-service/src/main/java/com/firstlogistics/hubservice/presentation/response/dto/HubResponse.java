package com.firstlogistics.hubservice.presentation.response.dto;

import com.firstlogistics.hubservice.application.dto.result.HubResult;

import java.util.UUID;

public record HubResponse(
        UUID hubId,
        String name,
        String roadAddress,
        GeoLocationResponse geoLocation,
        String status
) {
    public static HubResponse from(HubResult result){
        return new HubResponse(
                result.HubId(),
                result.name(),
                result.roadAddress(),
                GeoLocationResponse.of(result.latitude(), result.longitude()),
                result.status().name()
        );
    }

    public record GeoLocationResponse(
            double latitude,
            double longitude
    ){
        public static GeoLocationResponse of(double latitude, double longitude){
            return new GeoLocationResponse(
                    latitude,
                    longitude
            );
        }
    }
}
