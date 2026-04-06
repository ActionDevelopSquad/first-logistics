package com.firstlogistics.productservice.product.presentation.dto.request;

import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotNull UUID companyId,
        @NotBlank String name,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) Integer stock
) {
    public CreateProductCommand toCommand(UUID requesterId, String requesterRole) {
        return new CreateProductCommand(requesterId, requesterRole, companyId, name, price, stock);
    }
}
