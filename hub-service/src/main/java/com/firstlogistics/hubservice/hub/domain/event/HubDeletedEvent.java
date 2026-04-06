package com.firstlogistics.hubservice.hub.domain.event;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;

import java.util.UUID;

public record HubDeletedEvent(UUID hubId) {
    public static HubDeletedEvent from(Hub hub){
        return new HubDeletedEvent(hub.getId().id());
    }
}
