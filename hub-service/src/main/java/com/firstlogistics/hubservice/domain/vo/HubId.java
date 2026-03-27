package com.firstlogistics.hubservice.domain.vo;

import java.util.UUID;


public record HubId(UUID id) {

    public static HubId of(UUID id){
        return new HubId(id);
    }
    public static HubId generate(){
        return new HubId(UUID.randomUUID());
    }
}
