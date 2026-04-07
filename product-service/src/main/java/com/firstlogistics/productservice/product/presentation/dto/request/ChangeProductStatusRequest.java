package com.firstlogistics.productservice.product.presentation.dto.request;

import com.firstlogistics.productservice.product.application.dto.command.ChangeProductStatusCommand;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ChangeProductStatusRequest(
        @NotBlank(message = "상태 값은 필수입니다.")
        String status
) {
    public ChangeProductStatusCommand toCommand(UUID requesterId, String requesterRole, UUID productId) {
        return new ChangeProductStatusCommand(requesterId, requesterRole, productId, status);
    }
}
