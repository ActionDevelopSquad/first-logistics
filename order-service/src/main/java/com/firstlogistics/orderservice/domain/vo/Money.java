package com.firstlogistics.orderservice.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Money {
    private Long amount;

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
