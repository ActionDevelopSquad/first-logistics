package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

public record Time(int minutes) {
    private static final int MIN_MINUTES = 1;
    private static final int MAX_MINUTES = 1440;

    public Time {
        validate(minutes);
    }

    public static Time of(int minutes){
        return new Time(minutes);
    }

    private static void validate(int minutes){
        if(minutes < MIN_MINUTES || minutes >MAX_MINUTES)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_MINUTES_RANGE);
    }
}
