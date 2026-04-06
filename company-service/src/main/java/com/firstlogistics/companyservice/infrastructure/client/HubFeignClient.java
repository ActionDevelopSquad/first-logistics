package com.firstlogistics.companyservice.infrastructure.client;

import common.response.FeignApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service", path = "/api/v1/hubs")
public interface HubFeignClient {

    @GetMapping("/nearest")
    FeignApiResponse<UUID> findNearestHubId(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude
    );
}
