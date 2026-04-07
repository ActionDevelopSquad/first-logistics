package com.firstlogistics.hubservice.hubconnection.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResponse(
        @JsonProperty("id") UUID companyId,
        UUID hubId,
        double latitude,
        double longitude
) {
}
