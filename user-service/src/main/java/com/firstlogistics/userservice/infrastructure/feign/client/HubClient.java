package com.firstlogistics.userservice.infrastructure.feign.client;

import common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", url = "http://localhost:8082")
public interface HubClient {

    @GetMapping("/api/v1/hubs/{hubId}")
    ApiResponse<UUID> existsHub(@PathVariable("hubId") UUID hubId);
}
