package com.firstlogistics.deliverservice.application.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResponse(
	@JsonProperty("id") UUID companyId,
	UUID hubId,
	String name,
	String roadAddress,
	String detailAddress
) {
}
