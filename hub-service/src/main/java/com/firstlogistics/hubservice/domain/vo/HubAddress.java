package com.firstlogistics.hubservice.domain.vo;



public record HubAddress(String roadAddress) {
    public static HubAddress of(String roadAddress){
        return new HubAddress(roadAddress);
    }
}
