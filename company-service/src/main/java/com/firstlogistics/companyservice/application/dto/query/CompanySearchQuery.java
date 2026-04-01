package com.firstlogistics.companyservice.application.dto.query;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public record CompanySearchQuery(
        String keyword,
        String type,
        UUID hubId,
        String status,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public CompanyQueryCondition toCondition() {
        CompanyStatus companyStatus = status != null ? CompanyStatus.valueOf(status.toUpperCase()) : null;
        return new CompanyQueryCondition(keyword, type, hubId, companyStatus);
    }

    public Pageable toPageable() {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection)
                .orElse(Sort.Direction.DESC);
        String sortField = "updatedAt".equalsIgnoreCase(sortBy) ? "updatedAt" : "createdAt";
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}