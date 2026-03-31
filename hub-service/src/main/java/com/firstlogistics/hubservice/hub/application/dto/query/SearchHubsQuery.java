package com.firstlogistics.hubservice.hub.application.dto.query;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSearchDto;

public record SearchHubsQuery(
    String name,
    HubStatus status,
    Double latitude,
    Double longitude
) {
    public HubSearchDto toDto(){
        return new HubSearchDto(
                name,
                status,
                latitude,
                longitude
        );
    }
}
