package com.firstlogistics.hubservice.hub.presentation.dto.response;

import com.firstlogistics.hubservice.hub.application.dto.result.HubSummaryResult;

import java.util.List;

public record HubListResponse(
        List<HubSummaryResponse> hubList
) {
    public static HubListResponse from(List<HubSummaryResult> result){
        return new HubListResponse(
                result.stream().map(HubSummaryResponse::from).toList()
        );
    }
}
