package com.firstlogistics.orderservice.infrastructure.feign;

import com.firstlogistics.orderservice.application.port.dto.CompanyResponse;
import common.response.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", url = "${app.services.company-service.url}")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/{companyId}")
    FeignApiResponse<CompanyResponse> getCompanyById(@PathVariable UUID companyId);
}