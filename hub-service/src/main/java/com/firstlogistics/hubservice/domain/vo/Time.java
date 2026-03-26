package com.firstlogistics.hubservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Time {
    private final int minutes;

    private Time(int minutes){
        this.minutes = minutes;
    }
    public static Time of(int minutes){
        return new Time(minutes);
    }
}
