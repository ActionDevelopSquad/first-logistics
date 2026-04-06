package com.firstlogistics.hubservice.hubManager.domain.entity;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubManager {
    private HubManagerId id;
    private UserId userId;
    private HubId hubId;

    public static HubManager create(UserId userId, HubId hubId){
        validateUserId(userId);
        validateHubId(hubId);
        return new HubManager(HubManagerId.generate(), userId, hubId);
    }

    public static HubManager reconstitute(
            HubManagerId id,
            UserId userId,
            HubId hubId
    ){
        validateHubManagerId(id);
        validateUserId(userId);
        validateHubId(hubId);
        return new HubManager(id, userId, hubId);
    }
    private static void validateHubManagerId(HubManagerId id){
        if(id == null)
            throw new HubManagerException(HubManagerErrorCode.INVALID_HUB_MANAGER_ID);
    }
    private static void validateUserId(UserId userId){
        if(userId == null)
            throw new HubManagerException(HubManagerErrorCode.INVALID_USER_ID);
    }
    private static void validateHubId(HubId hubId){
        if(hubId == null)
            throw new HubManagerException(HubManagerErrorCode.INVALID_HUB_ID);
    }
    public void changeHub(HubId hubId){
        this.hubId = hubId;
    }
}
