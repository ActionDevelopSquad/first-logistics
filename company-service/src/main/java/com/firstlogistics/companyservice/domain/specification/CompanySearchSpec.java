package com.firstlogistics.companyservice.domain.specification;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;

import java.util.UUID;

public record CompanySearchSpec(
        String keyword,
        String type,
        UUID hubId,
        CompanyStatus status
) {
}
