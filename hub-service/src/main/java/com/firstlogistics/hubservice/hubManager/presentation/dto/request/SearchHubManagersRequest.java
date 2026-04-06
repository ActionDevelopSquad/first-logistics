package com.firstlogistics.hubservice.hubManager.presentation.dto.request;

import com.firstlogistics.hubservice.hubManager.application.dto.query.SearchHubManagersQuery;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record SearchHubManagersRequest(
        @Size(max = 200) List<UUID> userIds,
        @Size(max = 200) List<UUID> hubIds
) {
    public SearchHubManagersQuery toQuery(){
        return new SearchHubManagersQuery(
                userIds,
                hubIds
        );
    }
}
