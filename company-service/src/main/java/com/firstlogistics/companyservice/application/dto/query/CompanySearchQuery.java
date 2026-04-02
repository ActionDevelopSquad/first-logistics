package com.firstlogistics.companyservice.application.dto.query;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.specification.CompanySearchSpec;
import java.util.UUID;

public record CompanySearchQuery(
        String keyword,
        String type,
        UUID hubId,
        String status
) {
    public CompanySearchSpec toSpec() {
        CompanyStatus companyStatus = null;
        if (status != null) {
            try {
                companyStatus = CompanyStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_STATUS);
            }
        }

        return new CompanySearchSpec(keyword, type, hubId, companyStatus);
    }
}