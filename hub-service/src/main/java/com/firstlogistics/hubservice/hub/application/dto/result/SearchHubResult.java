package com.firstlogistics.hubservice.hub.application.dto.result;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubPageDto;

import java.util.UUID;

public record SearchHubResult(
        UUID hubId,
        String name,
        HubStatus status
) {
    public static SearchHubResult from(HubPageDto dto){
        return new SearchHubResult(
          dto.hubId(),
          dto.name(),
          dto.status()
        );
    }
}
