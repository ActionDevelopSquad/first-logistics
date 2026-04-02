package com.firstlogistics.deliverservice.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubResponse(
	UUID hubId,
	String name,
	String roadAddress
) {
}
