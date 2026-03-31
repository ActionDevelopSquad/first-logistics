package com.firstlogistics.deliverservice.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubRouteStepResponse(
	UUID sourceHubId,
	UUID destinationHubId,
	int distanceMeters,
	int durationMinutes
) {
}
