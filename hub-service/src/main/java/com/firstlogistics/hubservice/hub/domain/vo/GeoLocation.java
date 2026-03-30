package com.firstlogistics.hubservice.hub.domain.vo;


import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;

public record GeoLocation(double latitude, double longitude) {

    public GeoLocation{
        validate(latitude, longitude);
    }
    public static GeoLocation of (double latitude, double longitude){
        return new GeoLocation(latitude, longitude);
    }

    private static void validate(double latitude, double longitude){
        if(Double.isNaN(latitude)|| Double.isInfinite(latitude) || Double.isNaN(longitude) || Double.isInfinite(longitude))
            throw new HubException(HubErrorCode.INVALID_HUB_GEOLOCATION);
        if(latitude < -90.0 || latitude >90.0 || longitude <-180.0 || longitude >180.0)
            throw new HubException(HubErrorCode.INVALID_HUB_GEOLOCATION_RANGE);
    }
}
