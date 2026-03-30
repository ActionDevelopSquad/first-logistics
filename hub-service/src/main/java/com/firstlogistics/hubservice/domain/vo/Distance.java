package com.firstlogistics.hubservice.domain.vo;


public record Distance(int meters) {
    public static Distance of(int meters){
        return new Distance(meters);
    }
}
