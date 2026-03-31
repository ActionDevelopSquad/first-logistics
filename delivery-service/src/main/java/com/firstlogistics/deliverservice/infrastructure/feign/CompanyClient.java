package com.firstlogistics.deliverservice.infrastructure.feign;

import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.config.FeignErrorDecoder;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", configuration = FeignErrorDecoder.class)
public interface CompanyClient {

	@GetMapping("/api/v1/companies/{companyId}")
	FeignResponse<CompanyResponse> getCompany(@PathVariable UUID companyId);
}
