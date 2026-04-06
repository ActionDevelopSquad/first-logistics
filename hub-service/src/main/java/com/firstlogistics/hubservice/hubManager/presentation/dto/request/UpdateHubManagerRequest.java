package com.firstlogistics.hubservice.hubManager.presentation.dto.request;

import com.firstlogistics.hubservice.hubManager.application.dto.command.UpdateHubManagerCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateHubManagerRequest(
        @NotNull
        UUID hubId
) {
    public UpdateHubManagerCommand toCommand(){
        return new UpdateHubManagerCommand(
                hubId
        );
    }
}
