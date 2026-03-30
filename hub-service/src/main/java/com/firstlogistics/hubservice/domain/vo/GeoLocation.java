package com.firstlogistics.hubservice.domain.vo;


public record GeoLocation(double latitude, double longitude) {
    public static GeoLocation of (double latitude, double longitude){
        return new GeoLocation(latitude, longitude);
    }
}
