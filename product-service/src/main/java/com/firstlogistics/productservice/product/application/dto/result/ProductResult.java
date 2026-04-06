package com.firstlogistics.productservice.product.application.dto.result;

import com.firstlogistics.productservice.product.domain.entity.Product;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResult(
        UUID id,
        UUID companyId,
        UUID hubId,
        String name,
        BigDecimal price,
        String status
) {
    public static ProductResult from(Product product) {
        return new ProductResult(
                product.getId(),
                product.getCompanyId(),
                product.getHubId(),
                product.getName(),
                product.getPrice().amount(),
                product.getStatus().name()
        );
    }
}
