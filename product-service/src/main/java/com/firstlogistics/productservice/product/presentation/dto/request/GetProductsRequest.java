package com.firstlogistics.productservice.product.presentation.dto.request;

import com.firstlogistics.productservice.product.application.dto.query.ProductSearchQuery;
import java.util.UUID;

public record GetProductsRequest(
        String keyword,
        UUID companyId,
        UUID hubId,
        String status
) {
    public ProductSearchQuery toQuery() {
        return new ProductSearchQuery(keyword, companyId, hubId, status);
    }
}
