package com.firstlogistics.productservice.product.domain.specification;

import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import java.util.UUID;

public record ProductSearchSpec(
        String keyword,
        UUID companyId,
        UUID hubId,
        ProductStatus status
) {}
