package com.firstlogistics.productservice.product.presentation.dto.request;

import com.firstlogistics.productservice.product.application.dto.command.UpdateProductCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        @Size(min = 1, max = 100, message = "상품 이름은 1자 이상 100자 이하여야 합니다.")
        String name,

        @DecimalMin(value = "0.0", message = "가격은 0원 이상이어야 합니다.")
        BigDecimal price
) {
    public UpdateProductCommand toCommand(UUID requesterId, String requesterRole, UUID productId) {
        return new UpdateProductCommand(requesterId, requesterRole, productId, name, price);
    }
}
