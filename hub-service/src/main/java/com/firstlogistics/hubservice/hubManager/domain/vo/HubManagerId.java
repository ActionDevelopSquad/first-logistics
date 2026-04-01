package com.firstlogistics.hubservice.hubManager.domain.vo;

import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;

import java.util.UUID;

public record HubManagerId(
        UUID id
) {
    public HubManagerId{
        if(id == null)
            throw new HubManagerException(HubManagerErrorCode.INVALID_HUB_MANAGER_ID);
    }
    public static HubManagerId generate(){
        return new HubManagerId(UUID.randomUUID());
    }
    public static HubManagerId of(UUID id){
        return new HubManagerId(id);
    }
}
