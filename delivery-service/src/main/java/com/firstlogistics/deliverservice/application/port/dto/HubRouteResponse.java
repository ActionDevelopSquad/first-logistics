package com.firstlogistics.deliverservice.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubRouteResponse(
	UUID sourceId,
	UUID destinationId,
	List<HubRouteStepResponse> routes
) {
	public UUID sourceHubId() { return sourceId; }
	public UUID destinationHubId() { return destinationId; }
}
