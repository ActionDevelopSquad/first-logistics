package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;

import java.util.UUID;

public interface CompanyPort {

	CompanyResponse getCompany(UUID companyId);

	CompanyResponse getCompanyByUserId(UUID userId);
}
