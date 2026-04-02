package com.firstlogistics.hubservice.hub.domain.repository.dto;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;

import java.util.UUID;

public record HubSummaryDto(
        UUID hubId,
        String name,
        String roadAddress,
        HubStatus status
) {
}
