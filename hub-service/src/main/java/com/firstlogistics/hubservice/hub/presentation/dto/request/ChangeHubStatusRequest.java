package com.firstlogistics.hubservice.hub.presentation.dto.request;

import com.firstlogistics.hubservice.hub.application.dto.command.ChangeHubStatusCommand;
import jakarta.validation.constraints.NotBlank;

public record ChangeHubStatusRequest(
        @NotBlank
        String status
) {
    public ChangeHubStatusCommand toCommand(){
        return new ChangeHubStatusCommand(
           status
        );
    }
}
