package com.firstlogistics.orderservice.application.port;

import com.firstlogistics.orderservice.application.port.dto.CompanyResponse;

import java.util.UUID;

public interface CompanyPort {

    CompanyResponse getCompanyById(UUID companyId);
}