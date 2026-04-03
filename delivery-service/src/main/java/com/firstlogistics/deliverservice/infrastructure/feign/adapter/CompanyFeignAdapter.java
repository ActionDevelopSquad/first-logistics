package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyFeignAdapter implements CompanyPort {

	private final CompanyClient companyClient;

	@Override
	public CompanyResponse getCompany(UUID companyId) {
		return companyClient.getCompany(companyId).data();
	}

	@Override
	public CompanyResponse getCompanyByUserId(UUID userId) {
		List<CompanyResponse> results = companyClient.getCompaniesByUserId(userId).data();
		if (results == null || results.size() != 1) {
			throw new DeliveryException(DeliveryErrorCode.COMPANY_MANAGER_NOT_FOUND);
		}
		return results.get(0);
	}
}
