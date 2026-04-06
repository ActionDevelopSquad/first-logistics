package com.firstlogistics.hubservice.hub.application.dto.result;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubDetailsDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HubDetailsResult(
        UUID hubId,
        String name,
        String roadAddress,
        double latitude,
        double longitude,
        HubStatus status,
        HubType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static HubDetailsResult from(HubDetailsDto dto){
        return new HubDetailsResult(
                dto.hubId(),
                dto.name(),
                dto.roadAddress(),
                dto.latitude(),
                dto.longitude(),
                dto.status(),
                dto.type(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}
