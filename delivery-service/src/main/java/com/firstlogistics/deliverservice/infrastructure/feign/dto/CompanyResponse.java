package com.firstlogistics.deliverservice.infrastructure.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResponse(
	UUID companyId,
	UUID hubId
) {
}
