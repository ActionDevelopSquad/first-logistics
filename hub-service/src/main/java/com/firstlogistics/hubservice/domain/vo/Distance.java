package com.firstlogistics.hubservice.domain.vo;


import com.firstlogistics.hubservice.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.domain.exception.HubException;

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
            throw new HubException(HubErrorCode.INVALID_METERS_RANGE);
        }
    }
}
