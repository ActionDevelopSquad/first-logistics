package com.firstlogistics.companyservice.application.dto.result;

import com.firstlogistics.companyservice.domain.entity.Company;
import java.util.UUID;

public record CompanyResult(
        UUID id,
        UUID hubId,
        UUID managerId,
        String name,
        String type,
        String status,
        String roadAddress,
        String detailAddress,
        double latitude,
        double longitude
) {
    public static CompanyResult from(Company company) {
        return new CompanyResult(
                company.getId(),
                company.getHubId(),
                company.getManagerId(),
                company.getName(),
                company.getType().getClass().getSimpleName().toUpperCase(),
                company.getStatus().name(),
                company.getAddress().roadAddress(),
                company.getAddress().detailAddress(),
                company.getGeoLocation().latitude(),
                company.getGeoLocation().longitude()
        );
    }
}
