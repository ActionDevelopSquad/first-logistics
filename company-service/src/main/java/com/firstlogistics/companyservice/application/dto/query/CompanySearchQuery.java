package com.firstlogistics.companyservice.application.dto.query;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    public Pageable toPageable() {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection)
                .orElse(Sort.Direction.DESC);
        String sortField = "updatedAt".equalsIgnoreCase(sortBy) ? "updatedAt" : "createdAt";
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}