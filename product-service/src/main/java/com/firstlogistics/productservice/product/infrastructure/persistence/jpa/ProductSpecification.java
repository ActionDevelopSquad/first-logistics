package com.firstlogistics.productservice.product.infrastructure.persistence.jpa;

import static com.firstlogistics.productservice.product.infrastructure.persistence.jpa.QProductJpaEntity.productJpaEntity;

import com.firstlogistics.productservice.product.domain.specification.ProductSearchSpec;
import com.querydsl.core.BooleanBuilder;

public class ProductSpecification {

    private ProductSpecification() {}

    public static BooleanBuilder from(ProductSearchSpec spec) {
        BooleanBuilder builder = new BooleanBuilder();

        if (spec.keyword() != null && !spec.keyword().isBlank()) {
            builder.and(productJpaEntity.name.containsIgnoreCase(spec.keyword()));
        }
        if (spec.companyId() != null) {
            builder.and(productJpaEntity.companyId.eq(spec.companyId()));
        }
        if (spec.hubId() != null) {
            builder.and(productJpaEntity.hubId.eq(spec.hubId()));
        }
        if (spec.status() != null) {
            builder.and(productJpaEntity.status.eq(spec.status()));
        }

        return builder;
    }
}
