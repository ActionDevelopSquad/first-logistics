package com.firstlogistics.productservice.product.domain.entity;

import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.vo.Money;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    private UUID id;
    private UUID companyId;
    private String name;
    private Money price;
    private ProductStatus status;

    public static Product create(UUID companyId, String name, Money price) {
        validate(companyId, name);

        return new Product(
                UUID.randomUUID(),
                companyId,
                name,
                price,
                ProductStatus.SELLING
        );
    }

    private static void validate(UUID companyId, String name) {
        if (companyId == null) {
            throw new ProductException(ProductErrorCode.INVALID_COMPANY_ID);
        }
        if (name == null || name.isBlank()) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_NAME);
        }
    }

    public void changePrice(Money price) {
        this.price = price;
    }

    public void stopSelling() {
        this.status = ProductStatus.STOPPED;
    }

    public boolean isSellable() {
        return this.status == ProductStatus.SELLING;
    }
}
