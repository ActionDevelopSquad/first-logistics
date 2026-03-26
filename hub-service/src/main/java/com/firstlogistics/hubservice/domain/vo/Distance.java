package com.firstlogistics.hubservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Distance {
    private final int meters;

    private Distance(int meters){
        this.meters = meters;
    }
    public static Distance of(int meters){
        return new Distance(meters);
    }
}
