package com.firstlogistics.deliverservice.infrastructure.feign.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeignResponse<T>(T data) {
}
