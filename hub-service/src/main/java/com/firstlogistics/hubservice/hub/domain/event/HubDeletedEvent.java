package com.firstlogistics.hubservice.hub.domain.event;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;

import java.util.UUID;

public record HubDeletedEvent(UUID hubId, UUID deleterId) {
    public static HubDeletedEvent from(Hub hub, UUID userId){
        return new HubDeletedEvent(hub.getId().id(), userId);
    }
}
