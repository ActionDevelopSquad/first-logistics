package com.firstlogistics.hubservice.hubconnection.domain.vo;

import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;

public record RouteHub(
        HubId id,
        HubStatus status,
        HubType type
) {
    public static RouteHub from(Hub hub){
        return new RouteHub(
                hub.getId(),
                hub.getStatus(),
                hub.getType()
        );
    }
    public boolean isActive(){
        return status == HubStatus.ACTIVE;
    }
}
