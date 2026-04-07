package com.firstlogistics.deliverservice.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubRouteStepResponse(
	int hubRouteSequence,
	UUID sourceId,
	UUID destinationId,
	int distanceMeters,
	int durationMinutes
) {
	public UUID sourceHubId() { return sourceId; }
	public UUID destinationHubId() { return destinationId; }
}
