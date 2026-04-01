package com.firstlogistics.companyservice.presentation.dto.request;

import com.firstlogistics.companyservice.application.dto.query.CompanySearchQuery;

import java.util.UUID;

public record GetCompaniesRequest(
        String keyword,
        String type,
        UUID hubId,
        String status,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public CompanySearchQuery toQuery() {
        return new CompanySearchQuery(keyword, type, hubId, status, page, size, sortBy, sortDirection);
    }
}