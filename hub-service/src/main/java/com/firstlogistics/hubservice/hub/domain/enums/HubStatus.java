package com.firstlogistics.hubservice.hub.domain.enums;


public enum HubStatus {
    ACTIVE("정상 가동"),
    INACTIVE("운행 중단");


    private final String description;

    HubStatus(String description){
        this.description = description;
    }
}
