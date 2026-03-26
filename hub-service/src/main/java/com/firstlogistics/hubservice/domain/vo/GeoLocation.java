package com.firstlogistics.hubservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GeoLocation {
    private final double latitude;
    private final double longitude;

    private GeoLocation (double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public static GeoLocation of (double latitude, double longitude){
        return new GeoLocation(latitude, longitude);
    }
}
