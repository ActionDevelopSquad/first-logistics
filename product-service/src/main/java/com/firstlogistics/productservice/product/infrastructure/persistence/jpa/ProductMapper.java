package com.firstlogistics.productservice.product.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.vo.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductMapper {

    public static ProductJpaEntity toJpaEntity(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getCompanyId(),
                product.getHubId(),
                product.getName(),
                product.getPrice().amount(),
                product.getStatus()
        );
    }

    public static Product toDomain(ProductJpaEntity entity) {
        return Product.reconstitute(
                entity.getId(),
                entity.getCompanyId(),
                entity.getHubId(),
                entity.getName(),
                new Money(entity.getPrice()),
                entity.getStatus()
        );
    }
}
