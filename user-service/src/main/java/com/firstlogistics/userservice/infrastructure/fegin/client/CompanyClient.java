package com.firstlogistics.userservice.infrastructure.fegin.client;

import common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/{companyId}")
    ApiResponse<UUID> existsCompany(@PathVariable("companyId") UUID companyId);
}