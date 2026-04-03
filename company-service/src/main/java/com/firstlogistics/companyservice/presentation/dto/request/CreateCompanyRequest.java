package com.firstlogistics.companyservice.presentation.dto.request;

import com.firstlogistics.companyservice.application.dto.command.CreateCompanyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCompanyRequest(
        @NotNull UUID managerId,
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String roadAddress,
        @NotBlank String detailAddress,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
    public CreateCompanyCommand toCommand() {
        return new CreateCompanyCommand(managerId, name, type, roadAddress, detailAddress, latitude, longitude);
    }
}
