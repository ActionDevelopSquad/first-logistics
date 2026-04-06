package com.firstlogistics.hubservice.hubconnection.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RouteMetricResponse(
        int distanceMeters,
        int durationMinutes
) {
}
