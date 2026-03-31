package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;

import java.util.UUID;

public record HubConnectionId(UUID id) {
    public HubConnectionId{
        if(id== null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_ID);
    }

    public static HubConnectionId of(UUID id){
        return new HubConnectionId(id);
    }

    public static HubConnectionId generate(){
        return new HubConnectionId(UUID.randomUUID());
    }
}
