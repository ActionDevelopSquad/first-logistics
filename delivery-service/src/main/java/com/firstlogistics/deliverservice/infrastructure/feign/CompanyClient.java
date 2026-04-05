package com.firstlogistics.deliverservice.infrastructure.feign;

import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.config.FeignErrorDecoder;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", url = "${company-service.url:}", configuration = FeignErrorDecoder.class)
public interface CompanyClient {

	@GetMapping("/api/v1/companies/{companyId}")
	FeignApiResponse<CompanyResponse> getCompany(@PathVariable("companyId") UUID companyId);

	@GetMapping("/api/v1/companies/manager/{managerId}")
	FeignApiResponse<CompanyResponse> getCompanyManager(@PathVariable("managerId") UUID managerId);
}
