package com.firstlogistics.orderservice.domain.vo;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

public record Money(
        Long amount
) {
    public Money {
        if (amount == null) {
            throw new OrderException(OrderErrorCode.INVALID_MONEY_AMOUNT);
        }
        if (amount < 0) {
            throw new OrderException(OrderErrorCode.INVALID_MONEY_AMOUNT);
        }
    }

    public static Money of(Long amount) {
        return new Money(amount);
    }

    public Money add(Money money) {
        return new Money(this.amount + money.amount);
    }

    public Money multiply(int quantity) {
        return new Money(this.amount * quantity);
    }
}
