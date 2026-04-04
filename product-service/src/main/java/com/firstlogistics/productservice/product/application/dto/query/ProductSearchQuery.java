package com.firstlogistics.productservice.product.application.dto.query;

import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.specification.ProductSearchSpec;
import java.util.UUID;

public record ProductSearchQuery(
        String keyword,
        UUID companyId,
        UUID hubId,
        String status
) {
    public ProductSearchSpec toSpec() {
        ProductStatus productStatus = null;
        if (status != null) {
            try {
                productStatus = ProductStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ProductException(ProductErrorCode.INVALID_PRODUCT_STATUS);
            }
        }
        return new ProductSearchSpec(keyword, companyId, hubId, productStatus);
    }
}
