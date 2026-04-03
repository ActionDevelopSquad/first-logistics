package com.firstlogistics.companyservice.presentation.dto.request;

import com.firstlogistics.companyservice.application.dto.command.UpdateCompanyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCompanyRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String roadAddress,
        @NotBlank String detailAddress,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
    public UpdateCompanyCommand toCommand(UUID companyId) {
        return new UpdateCompanyCommand(companyId, name, type, roadAddress, detailAddress, latitude, longitude);
    }
}
