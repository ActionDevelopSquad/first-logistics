package com.firstlogistics.companyservice.presentation.dto.request;

import com.firstlogistics.companyservice.application.dto.query.CompanySearchQuery;

import java.util.UUID;

public record GetCompaniesRequest(
        String keyword,
        String type,
        UUID hubId,
        String status
) {
    public CompanySearchQuery toQuery() {
        return new CompanySearchQuery(keyword, type, hubId, status);
    }
}