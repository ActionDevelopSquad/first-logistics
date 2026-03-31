package com.firstlogistics.hubservice.hubconnection.domain.vo;


import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

public record Distance(int meters) {
    private static final int MIN_METERS = 0;
    private static final int MAX_METERS = 1000000;


    public Distance{
        validate(meters);
    }
    public static Distance of(int meters){
        return new Distance(meters);
    }
    private static void validate(int meters){
        if(meters<MIN_METERS || meters >MAX_METERS){
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_METERS_RANGE);
        }
    }
}
