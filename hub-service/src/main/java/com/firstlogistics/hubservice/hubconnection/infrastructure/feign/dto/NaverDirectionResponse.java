package com.firstlogistics.hubservice.hubconnection.infrastructure.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverDirectionResponse(
        int code,
        String message,
        Route route
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(
            List<Traoptimal> traoptimal
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Traoptimal(
            Summary summary
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Summary(
            int distance,
            int duration
    ) {
    }
}
