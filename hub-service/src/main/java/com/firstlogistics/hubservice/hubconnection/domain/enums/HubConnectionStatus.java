package com.firstlogistics.hubservice.hubconnection.domain.enums;


import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

public enum HubConnectionStatus {
    ACTIVE("연결 활성화"),
    INACTIVE("연결 비활성화");


    private final String description;

    HubConnectionStatus(String description){
        this.description = description;
    }

    public static HubConnectionStatus from(String status){
        if (status == null || status.isBlank()) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_STATUS);
        }

        try {
            return HubConnectionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_STATUS);
        }
    }
}
