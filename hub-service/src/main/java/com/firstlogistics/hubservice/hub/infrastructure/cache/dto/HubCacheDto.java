package com.firstlogistics.hubservice.hub.infrastructure.cache.dto;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;

import java.util.UUID;

public record HubCacheDto(
        UUID id,
        String name,
        String roadAddress,
        double latitude,
        double longitude,
        HubStatus status,
        HubType type
) {
    public static HubCacheDto from(Hub hub) {
        return new HubCacheDto(
                hub.getId().id(),
                hub.getName(),
                hub.getAddress().roadAddress(),
                hub.getGeoLocation().latitude(),
                hub.getGeoLocation().longitude(),
                hub.getStatus(),
                hub.getType()
        );
    }

    public Hub toDomain() {
        return Hub.reconstitute(
                HubId.of(id),
                name,
                HubAddress.of(roadAddress),
                GeoLocation.of(latitude, longitude),
                status,
                type
        );
    }
}

