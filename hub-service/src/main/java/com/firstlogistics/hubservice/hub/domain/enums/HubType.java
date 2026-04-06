package com.firstlogistics.hubservice.hub.domain.enums;

import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;

public enum HubType {
    METROPOLITAN("광역 허브"),
    GENERAL("일반 허브");

    private final String description;

    HubType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public static HubType from(String value) {
        if (value == null || value.isBlank()) {
            throw new HubException(HubErrorCode.INVALID_HUB_TYPE);
        }

        String normalized = value.trim();
        for (HubType hubType : values()) {
            if (hubType.name().equalsIgnoreCase(normalized)
                    || hubType.description.equalsIgnoreCase(normalized)) {
                return hubType;
            }
        }

        throw new HubException(HubErrorCode.INVALID_HUB_TYPE);
    }
}
