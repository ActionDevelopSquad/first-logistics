package com.firstlogistics.companyservice.application.dto.command;

import java.util.UUID;

public record CreateCompanyCommand(
        UUID userId,
        String name,
        String type,
        String roadAddress,
        String detailAddress,
        double latitude,
        double longitude
) {
}
