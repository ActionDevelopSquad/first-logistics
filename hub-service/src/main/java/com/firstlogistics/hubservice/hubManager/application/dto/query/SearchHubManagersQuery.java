package com.firstlogistics.hubservice.hubManager.application.dto.query;

import com.firstlogistics.hubservice.hubManager.domain.specification.HubManagerSearchSpec;

import java.util.List;
import java.util.UUID;

public record SearchHubManagersQuery(
        List<UUID> userIds,
        List<UUID> hubIds
 ) {
    public SearchHubManagersQuery {
                userIds = userIds == null ? List.of() : List.copyOf(userIds);
                hubIds = hubIds == null ? List.of() : List.copyOf(hubIds);
            }

    public HubManagerSearchSpec toSpec(){
        return new HubManagerSearchSpec(
                userIds,
                hubIds
        );
    }
}
