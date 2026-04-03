package com.firstlogistics.hubservice.hubManager.presentation.dto.request;

import com.firstlogistics.hubservice.hubManager.application.dto.query.SearchHubManagersQuery;

import java.util.List;
import java.util.UUID;

public record SearchHubManagersRequest(
        List<UUID> userIds,
        List<UUID> hubIds
) {
    public SearchHubManagersQuery toQuery(){
        return new SearchHubManagersQuery(
                userIds,
                hubIds
        );
    }
}
