package com.firstlogistics.orderservice.domain.vo;

public record Money(
        Long amount
) {
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
