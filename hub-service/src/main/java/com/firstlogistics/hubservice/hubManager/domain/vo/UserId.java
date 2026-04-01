package com.firstlogistics.hubservice.hubManager.domain.vo;

import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;

import java.util.UUID;

public record UserId(UUID id) {
    public UserId{
        if(id == null)
            throw new HubManagerException(HubManagerErrorCode.INVALID_HUB_MANAGER_ID);
    }
    public static UserId of(UUID id){
        return new UserId(id);
    }
}
