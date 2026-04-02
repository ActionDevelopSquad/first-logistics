package com.firstlogistics.hubservice.hub.application.dto.query;

import com.firstlogistics.hubservice.hub.domain.specification.HubIdsSpec;

import java.util.List;
import java.util.UUID;

public record GetHubsQuery(
        List<UUID> ids
) {
    public HubIdsSpec toSpec(){
        return new HubIdsSpec(ids);
    }
}
