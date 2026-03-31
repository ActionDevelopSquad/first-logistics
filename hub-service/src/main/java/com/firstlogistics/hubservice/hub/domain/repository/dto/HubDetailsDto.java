package com.firstlogistics.hubservice.hub.domain.repository.dto;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubDetailsDto(
        UUID hubId,
        String name,
        String roadAddress,
        double latitude,
        double longitude,
        HubStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
