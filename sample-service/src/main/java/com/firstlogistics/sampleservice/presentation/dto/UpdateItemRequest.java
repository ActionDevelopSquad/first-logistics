package com.firstlogistics.sampleservice.presentation.dto;

import com.firstlogistics.sampleservice.application.command.UpdateItemCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateItemRequest(
        @NotBlank(message = "이름은 필수입니다.") String name,
        String description
) {
    public UpdateItemCommand toCommand(Long id) {
        return new UpdateItemCommand(id, name, description);
    }
}
