package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyFeignAdapter implements CompanyPort {

	private final CompanyClient companyClient;

	@Override
	public CompanyResponse getCompany(UUID companyId) {
		return companyClient.getCompany(companyId).data();
	}
}
