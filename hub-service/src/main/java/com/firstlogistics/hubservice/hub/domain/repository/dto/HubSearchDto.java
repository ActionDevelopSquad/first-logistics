package com.firstlogistics.hubservice.hub.domain.repository.dto;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;

public record HubSearchDto(
        String name,
        HubStatus status,
        Double latitude,
        Double longitude
) {
}
