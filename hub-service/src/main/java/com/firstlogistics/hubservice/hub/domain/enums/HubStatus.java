package com.firstlogistics.hubservice.hub.domain.enums;


import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;

public enum HubStatus {
    ACTIVE("정상 가동"),
    INACTIVE("운행 중단");


    private final String description;

    HubStatus(String description){
        this.description = description;
    }

    public static HubStatus from(String status) {
        if (status == null || status.isBlank()) {
            throw new HubException(HubErrorCode.INVALID_HUB_STATUS);
        }

        try {
            return HubStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HubException(HubErrorCode.INVALID_HUB_STATUS);
        }
    }
}
