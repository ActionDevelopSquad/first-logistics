package com.firstlogistics.hubservice.hub.domain.specification;

import java.util.List;
import java.util.UUID;

public record HubIdsSpec(
        List<UUID> ids
) {
}
