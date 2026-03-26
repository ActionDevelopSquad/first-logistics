package com.firstlogistics.hubservice.domain.vo;


import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class HubAddress {
    private final String roadAddress;

    private HubAddress (String roadAddress){
        this.roadAddress = roadAddress;
    }
    public static HubAddress of(String roadAddress){
        return new HubAddress(roadAddress);
    }
}
