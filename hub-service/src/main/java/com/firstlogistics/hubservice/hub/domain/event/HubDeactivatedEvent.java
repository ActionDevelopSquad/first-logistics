package com.firstlogistics.hubservice.hub.domain.event;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;

import java.util.UUID;

public record HubDeactivatedEvent(UUID hubId) {
    public static HubDeactivatedEvent from(Hub hub){
        return new HubDeactivatedEvent(hub.getId().id());
    }
}
