package com.firstlogistics.hubservice.hub.presentation.dto.request;

import com.firstlogistics.hubservice.hub.application.dto.query.GetHubsQuery;

import java.util.List;
import java.util.UUID;

public record GetHubsByIdsRequest(
        List<UUID> ids
) {
    public GetHubsQuery toQuery(){
        return new GetHubsQuery(ids);
    }
}
