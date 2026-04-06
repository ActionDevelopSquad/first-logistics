package com.firstlogistics.orderservice.infrastructure.feign;

import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;
import common.response.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", url = "${app.services.hub-service.url}")
public interface HubClient {

    @GetMapping("/api/v1/hub-managers/users/{userId}")
    FeignApiResponse<HubManagerResponse> getHubManagerInfo(@PathVariable UUID userId);

}