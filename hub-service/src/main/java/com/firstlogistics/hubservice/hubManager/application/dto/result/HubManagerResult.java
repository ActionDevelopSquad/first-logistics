package com.firstlogistics.hubservice.hubManager.application.dto.result;


import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;

import java.util.UUID;

public record HubManagerResult (
        UUID hubManagerId,
        UUID userId,
        UUID hubId
) {

    public static  HubManagerResult from(HubManager hubManager){
        return new HubManagerResult(
                hubManager.getId().id(),
                hubManager.getUserId().id(),
                hubManager.getHubId().id()
        );
    }

}
