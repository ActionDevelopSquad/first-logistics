package com.firstlogistics.hubservice.domain.vo;

import com.firstlogistics.hubservice.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.domain.exception.HubException;

import java.util.UUID;


public record HubId(UUID id) {

    public HubId{
        if(id== null)
            throw new HubException(HubErrorCode.INVALID_HUB_CONNECTION_ID);
    }

    public static HubId of(UUID id){
        return new HubId(id);
    }

    public static HubId generate(){
        return new HubId(UUID.randomUUID());
    }
}
