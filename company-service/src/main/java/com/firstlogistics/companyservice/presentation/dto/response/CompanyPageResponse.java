package com.firstlogistics.companyservice.presentation.dto.response;

import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public record CompanyPageResponse(
        List<CompanyItem> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static CompanyPageResponse from(Page<CompanyResult> page) {
        return new CompanyPageResponse(
                page.getContent().stream().map(CompanyItem::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public record CompanyItem(
            UUID id,
            UUID hubId,
            String name,
            String type,
            String status,
            String roadAddress,
            String detailAddress
    ) {
        public static CompanyItem from(CompanyResult result) {
            return new CompanyItem(
                    result.id(),
                    result.hubId(),
                    result.name(),
                    result.type(),
                    result.status(),
                    result.roadAddress(),
                    result.detailAddress()
            );
        }
    }
}