package com.firstlogistics.companyservice.presentation.dto.request;

import com.firstlogistics.companyservice.application.dto.command.ChangeManagerIdCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeManagerIdRequest(
        @NotNull UUID managerId
) {
    public ChangeManagerIdCommand toCommand(UUID companyId) {
        return new ChangeManagerIdCommand(companyId, managerId);
    }
}
