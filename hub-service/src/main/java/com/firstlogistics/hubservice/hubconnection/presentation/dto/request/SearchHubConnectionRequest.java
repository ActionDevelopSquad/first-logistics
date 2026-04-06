package com.firstlogistics.hubservice.hubconnection.presentation.dto.request;



import com.firstlogistics.hubservice.hubconnection.application.dto.query.SearchHubConnectionQuery;

import java.util.UUID;

public record SearchHubConnectionRequest(
        UUID sourceHubId,
        UUID destinationHubId,
        Integer time,
        Integer distance,
        String status
) {
    public SearchHubConnectionQuery toQuery(){
        return new SearchHubConnectionQuery(
                sourceHubId,
                destinationHubId,
                time,
                distance,
                status
        );
    }
}
