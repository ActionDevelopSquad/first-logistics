package com.firstlogistics.hubservice.hubManager.presentation.dto.response;

import com.firstlogistics.hubservice.hubManager.application.dto.result.HubManagerResult;

import java.util.UUID;

public record HubManagerResponse(
        UUID hubManagerId,
        UUID userId,
        UUID hubId
) {
    public static HubManagerResponse from(HubManagerResult result){
        return new HubManagerResponse(
                result.hubManagerId(),
                result.userId(),
                result.hubId()
        );
    }
}
