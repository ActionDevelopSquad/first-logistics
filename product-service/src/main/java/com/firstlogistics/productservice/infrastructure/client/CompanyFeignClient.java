package com.firstlogistics.productservice.infrastructure.client;

import com.firstlogistics.productservice.infrastructure.client.dto.CompanyClientResponse;
import common.response.FeignApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", path = "/api/v1/companies")
public interface CompanyFeignClient {

    @GetMapping("/{companyId}")
    FeignApiResponse<CompanyClientResponse> getCompany(@PathVariable UUID companyId);
}
