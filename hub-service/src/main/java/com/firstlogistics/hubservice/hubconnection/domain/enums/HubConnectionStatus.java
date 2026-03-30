package com.firstlogistics.hubservice.hubconnection.domain.enums;


public enum HubConnectionStatus {
    ACTIVE("연결 활성화"),
    INACTIVE("연결 비활성화");


    private final String description;

    HubConnectionStatus(String description){
        this.description = description;
    }
}
