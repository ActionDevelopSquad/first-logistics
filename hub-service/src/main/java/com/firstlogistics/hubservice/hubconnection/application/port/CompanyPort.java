package com.firstlogistics.hubservice.hubconnection.application.port;

import com.firstlogistics.hubservice.hubconnection.application.port.dto.CompanyResponse;
import com.firstlogistics.hubservice.hubconnection.domain.vo.CompanyId;

import java.util.UUID;

public interface CompanyPort {
    CompanyResponse getCompany(CompanyId companyId);
}
