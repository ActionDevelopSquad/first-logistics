package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyFeignAdapter implements CompanyPort {

	private final CompanyClient companyClient;

	@Override
	public CompanyResponse getCompany(UUID companyId) {
		log.info("[Feign] company-service 업체 조회 - companyId: {}", companyId);
		return companyClient.getCompany(companyId).data();
	}

	@Override
	public CompanyResponse getCompanyManager(UUID userId) {
		log.info("[Feign] company-service 업체 관리자 조회 - userId: {}", userId);
		return companyClient.getCompanyManager(userId).data();
	}
}
