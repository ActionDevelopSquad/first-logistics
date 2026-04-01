package com.firstlogistics.hubservice.hub.presentation.dto.request;

import com.firstlogistics.hubservice.hub.application.dto.query.SearchHubsQuery;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record SearchHubsRequest(
        String name,
        HubStatus status,
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude
) {
    public SearchHubsQuery toQuery(){
        return new SearchHubsQuery(
                name,
                status,
                latitude,
                longitude
        );
    }
}
