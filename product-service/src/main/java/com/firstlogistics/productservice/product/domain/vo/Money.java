package com.firstlogistics.productservice.product.domain.vo;

import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(
        BigDecimal amount
) {

    public Money {
        if (amount == null) {
            throw new ProductException(ProductErrorCode.INVALID_MONEY);
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException(ProductErrorCode.INVALID_MONEY);
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money krw(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
}
