package com.firstlogistics.companyservice.application.dto.query;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import java.util.UUID;

public record CompanySearchQuery(
        String keyword,
        String type,
        UUID hubId,
        String status
) {
    public CompanyQueryCondition toCondition() {
        CompanyStatus companyStatus = null;
        if (status != null) {
            try {
                companyStatus = CompanyStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_STATUS);
            }
        }

        return new CompanyQueryCondition(keyword, type, hubId, companyStatus);
    }
}