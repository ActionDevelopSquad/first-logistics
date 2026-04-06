package com.firstlogistics.userservice.infrastructure.feign.client;

import common.response.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/api/v1/deliveries/{deliveryId}")
    FeignApiResponse<?> existsDelivery(@PathVariable("deliveryId") UUID deliveryId);
}
