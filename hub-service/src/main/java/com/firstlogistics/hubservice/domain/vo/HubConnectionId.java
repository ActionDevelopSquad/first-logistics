package com.firstlogistics.hubservice.domain.vo;

import java.util.UUID;

public record HubConnectionId(UUID id) {
    public static HubConnectionId of(UUID id){
        return new HubConnectionId(id);
    }
    public static HubConnectionId generate(){
        return new HubConnectionId(UUID.randomUUID());
    }
}
