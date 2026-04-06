package com.firstlogistics.hubservice.hubconnection.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResponse(
        UUID companyId,
        UUID hubId,
        double latitude,
        double longitude
) {
}
