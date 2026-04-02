package com.firstlogistics.hubservice.hub.presentation.dto.response;

import com.firstlogistics.hubservice.hub.application.dto.result.HubSummaryResult;

import java.util.UUID;

public record HubSummaryResponse(
        UUID hubId,
        String name,
        String roadAddress,
        String status
) {
    public static HubSummaryResponse from(HubSummaryResult result) {
        return new HubSummaryResponse(
                result.hubId(),
                result.name(),
                result.roadAddress(),
                result.status().name()
        );
    }
}