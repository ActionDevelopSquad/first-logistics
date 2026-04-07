package com.firstlogistics.deliverservice.infrastructure.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HubListFeignResponse(
	List<HubResponse> hubList
) {
}
