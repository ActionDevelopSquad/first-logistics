package com.firstlogistics.sampleservice.presentation.dto;

import com.firstlogistics.sampleservice.application.command.CreateItemCommand;
import jakarta.validation.constraints.NotBlank;

public record CreateItemRequest(
        @NotBlank(message = "이름은 필수입니다.") String name,
        String description
) {
    public CreateItemCommand toCommand() {
        return new CreateItemCommand(name, description);
    }
}
