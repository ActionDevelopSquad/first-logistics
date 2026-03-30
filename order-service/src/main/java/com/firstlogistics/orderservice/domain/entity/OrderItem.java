package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.vo.Money;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {
    private UUID id;
    private UUID productId;
    private String productName;
    private Money unitPrice;
    private int quantity;
    private Money subTotal;

    static OrderItem create(
            UUID productId,
            String productName,
            Money unitPrice,
            int quantity
    ) {
        return new OrderItem(
                UUID.randomUUID(),
                productId,
                productName,
                unitPrice,
                quantity,
                unitPrice.multiply(quantity)
        );
    }

    /**
     * OrderItemJpaEntity -> OrderItem 변환 시에만 사용
     */
    public static OrderItem reconstitute(
            UUID id,
            UUID productId,
            String productName,
            Long unitPrice,
            int quantity,
            Long subTotal
    ) {
        return new OrderItem(
                id,
                productId,
                productName,
                Money.of(unitPrice),
                quantity,
                Money.of(subTotal)
        );
    }
}
