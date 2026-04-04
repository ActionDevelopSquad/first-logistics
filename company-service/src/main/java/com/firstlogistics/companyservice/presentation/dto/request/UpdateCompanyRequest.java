package com.firstlogistics.companyservice.presentation.dto.request;

import com.firstlogistics.companyservice.application.dto.command.UpdateCompanyCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCompanyRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String roadAddress,
        @NotBlank String detailAddress,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {
    public UpdateCompanyCommand toCommand(UUID companyId) {
        return new UpdateCompanyCommand(companyId, name, type, roadAddress, detailAddress, latitude, longitude);
    }
}
