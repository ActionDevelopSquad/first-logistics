package com.firstlogistics.hubservice.hubconnection.infrastructure.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeignApiResponse<T>(T data) {
}

