package com.firstlogistics.hubservice.hub.domain.event;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;

import java.util.UUID;

public record HubActivatedEvent(UUID hubId) {
    public static HubActivatedEvent from(Hub hub){
        return new HubActivatedEvent(hub.getId().id());
    }
}
