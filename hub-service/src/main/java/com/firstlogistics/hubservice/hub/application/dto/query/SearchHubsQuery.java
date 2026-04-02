package com.firstlogistics.hubservice.hub.application.dto.query;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSearchDto;

public record SearchHubsQuery(
    String name,
    HubStatus status,
    Double latitude,
    Double longitude
) {
    public SearchHubsQuery(String name, String status, Double latitude, Double longitude){
        this(name, parseStatus(status), latitude, longitude);
        if((latitude== null) != (longitude == null))
            throw new HubException(HubErrorCode.INVALID_HUB_SEARCH_COORDINATE);
    }

    public HubSearchDto toDto(){
        return new HubSearchDto(
                name,
                status,
                latitude,
                longitude
        );
    }
    private static HubStatus parseStatus(String status){
        if(status == null || status.isBlank())
            return null;
        try{
            return HubStatus.valueOf(status.toUpperCase());
        } catch(IllegalArgumentException e){
            throw new HubException(HubErrorCode.INVALID_HUB_STATUS);
        }
    }
}
