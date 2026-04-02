package com.firstlogistics.hubservice.hub.presentation.dto.response;

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