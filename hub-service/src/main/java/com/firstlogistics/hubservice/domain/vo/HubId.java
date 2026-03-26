package com.firstlogistics.hubservice.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class HubId {
    private final UUID id;

    private HubId(UUID id){
        this.id = id;
    }
    public static HubId of(UUID id){
        return new HubId(id);
    }
    public static HubId generate(){
        return new HubId(UUID.randomUUID());
    }
}
