package com.firstlogistics.companyservice.presentation.dto.response;

import com.firstlogistics.companyservice.application.dto.result.CompanyResult;

import java.util.UUID;

public record CompanyResponse(
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

    public static CompanyResponse from(CompanyResult result) {
        return new CompanyResponse(
                result.id(),
                result.hubId(),
                result.managerId(),
                result.name(),
                result.type(),
                result.status(),
                result.roadAddress(),
                result.detailAddress(),
                result.latitude(),
                result.longitude()
        );
    }
}
