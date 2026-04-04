package com.firstlogistics.productservice.product.domain.entity;

import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.vo.Money;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter
public class Product {

    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;
    private UUID companyId;
    private UUID hubId;
    private String name;
    private Money price;
    private ProductStatus status;

    public static Product create(UUID companyId, UUID hubId, String name, Money price) {
        validate(companyId, hubId, name);

        return new Product(
                UUID.randomUUID(),
                companyId,
                hubId,
                name,
                price,
                ProductStatus.SELLING
        );
    }

    public static Product reconstitute(UUID id, UUID companyId, UUID hubId, String name, Money price,
                                       ProductStatus status) {
        return new Product(id, companyId, hubId, name, price, status);
    }

    private static void validate(UUID companyId, UUID hubId, String name) {
        if (companyId == null) {
            throw new ProductException(ProductErrorCode.INVALID_COMPANY_ID);
        }
        if (hubId == null) {
            throw new ProductException(ProductErrorCode.INVALID_HUB_ID);
        }
        if (name == null || name.isBlank()) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_NAME);
        }
    }

    public void changePrice(Money price) {
        this.price = price;
    }

    public void stopSelling() {
        if (this.status == ProductStatus.STOPPED) {
            throw new ProductException(ProductErrorCode.PRODUCT_ALREADY_STOPPED);
        }
        this.status = ProductStatus.STOPPED;
    }

    public boolean isSellable() {
        return this.status == ProductStatus.SELLING;
    }
}
