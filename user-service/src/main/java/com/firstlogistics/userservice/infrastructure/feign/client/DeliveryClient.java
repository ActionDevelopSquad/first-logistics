package com.firstlogistics.userservice.infrastructure.feign.client;

import common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "delivery-service", url = "http://localhost:8084")
public interface DeliveryClient {

    @GetMapping("/api/v1/deliveries/{deliveryId}")
    ApiResponse<UUID> existsDelivery(@PathVariable("deliveryId") UUID deliveryId);
}
