package com.firstlogistics.productservice.product.presentation.dto.response;

import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID companyId,
        UUID hubId,
        String name,
        BigDecimal price,
        String status
) {
    public static ProductResponse from(ProductResult result) {
        return new ProductResponse(
                result.id(),
                result.companyId(),
                result.hubId(),
                result.name(),
                result.price(),
                result.status()
        );
    }
}
