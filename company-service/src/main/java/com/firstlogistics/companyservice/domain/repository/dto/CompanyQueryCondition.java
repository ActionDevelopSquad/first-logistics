package com.firstlogistics.companyservice.domain.repository.dto;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;

import java.util.UUID;

public record CompanyQueryCondition(
        String keyword,
        String type,
        UUID hubId,
        CompanyStatus status
) {
}