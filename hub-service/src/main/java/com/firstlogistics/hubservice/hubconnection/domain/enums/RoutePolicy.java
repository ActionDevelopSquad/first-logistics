package com.firstlogistics.hubservice.hubconnection.domain.enums;

import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

public enum RoutePolicy {
    HYBRID("p2p&hub2hub"),
    HUB_TO_HUB("hub2hub");

    private final String description;

    RoutePolicy(String description) {
        this.description = description;
    }
    public static RoutePolicy from(String value){
        try{
            return RoutePolicy.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_ROUTE_POLICY);
        }
    }
}
