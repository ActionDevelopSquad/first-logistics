package com.firstlogistics.deliverservice.infrastructure.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubRouteResponse(
	UUID sourceHubId,
	UUID destinationHubId,
	List<HubRouteStepResponse> routes
) {
}
