package com.firstlogistics.productservice.product.presentation.dto.response;

import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public record ProductPageResponse(
        List<ProductItem> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static ProductPageResponse from(Page<ProductResult> page) {
        return new ProductPageResponse(
                page.getContent().stream().map(ProductItem::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public record ProductItem(
            UUID id,
            UUID companyId,
            UUID hubId,
            String name,
            BigDecimal price,
            String status
    ) {
        public static ProductItem from(ProductResult result) {
            return new ProductItem(
                    result.id(),
                    result.companyId(),
                    result.hubId(),
                    result.name(),
                    result.price(),
                    result.status()
            );
        }
    }
}
