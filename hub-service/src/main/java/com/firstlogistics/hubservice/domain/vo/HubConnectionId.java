package com.firstlogistics.hubservice.domain.vo;

import com.firstlogistics.hubservice.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.domain.exception.HubException;

import java.util.UUID;

public record HubConnectionId(UUID id) {
    public HubConnectionId{
        if(id== null)
            throw new HubException(HubErrorCode.INVALID_HUB_CONNECTION_ID);
    }

    public static HubConnectionId of(UUID id){
        return new HubConnectionId(id);
    }

    public static HubConnectionId generate(){
        return new HubConnectionId(UUID.randomUUID());
    }
}
