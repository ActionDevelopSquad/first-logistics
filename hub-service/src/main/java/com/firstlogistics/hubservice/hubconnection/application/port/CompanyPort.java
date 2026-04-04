package com.firstlogistics.hubservice.hubconnection.application.port;

import com.firstlogistics.hubservice.hubconnection.application.port.dto.CompanyResponse;

import java.util.UUID;

public interface CompanyPort {
    CompanyResponse getCompany(UUID companyId);
}
