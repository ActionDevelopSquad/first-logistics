package com.firstlogistics.hubservice.hub.domain.vo;


import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;

public record HubAddress(String roadAddress) {
    private static final int MAX_LENGTH = 255;

    public HubAddress{
        validate(roadAddress);
    }
    public static HubAddress of(String roadAddress){
        return new HubAddress(roadAddress);
    }
    private static void validate(String roadAddress){
        if(roadAddress.isBlank() || roadAddress == null)
            throw new HubException(HubErrorCode.INVALID_HUB_ADDRESS);
        if(roadAddress.length()> MAX_LENGTH)
            throw new HubException(HubErrorCode.INVALID_HUB_ADDRESS);
    }
}
