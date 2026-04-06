package com.firstlogistics.productservice.product.application.port;

import java.util.UUID;

public interface CompanyPort {

    CompanyInfo getCompany(UUID companyId);

    record CompanyInfo(UUID companyId, UUID hubId, UUID managerId) {}
}
