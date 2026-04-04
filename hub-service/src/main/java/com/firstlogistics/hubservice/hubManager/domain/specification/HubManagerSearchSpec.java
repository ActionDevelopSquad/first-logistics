package com.firstlogistics.hubservice.hubManager.domain.specification;

import java.util.List;
import java.util.UUID;

public record HubManagerSearchSpec(
        List<UUID> userIds,
        List<UUID> hubIds
) {
}
