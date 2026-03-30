package com.firstlogistics.hubservice.domain.vo;

public record Time(int minutes) {
    public static Time of(int minutes){
        return new Time(minutes);
    }
}
