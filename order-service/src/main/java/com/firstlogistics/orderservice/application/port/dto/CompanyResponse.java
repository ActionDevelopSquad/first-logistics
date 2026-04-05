package com.firstlogistics.orderservice.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResponse(
        UUID id,
        UUID hubId,
        UUID managerId
) {
}