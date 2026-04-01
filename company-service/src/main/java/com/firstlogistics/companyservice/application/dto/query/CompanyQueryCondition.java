package com.firstlogistics.companyservice.application.dto.query;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;

import java.util.UUID;

public record CompanyQueryCondition(
        String keyword,
        String type,
        UUID hubId,
        CompanyStatus status
) {
}