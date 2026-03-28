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
    private OrderId orderId;
    private UUID productId;
    private String productName;
    private Money unitPrice;
    private int quantity;
    private Money subTotal;

    static OrderItem create(
            OrderId orderId,
            UUID productId,
            String productName,
            Money unitPrice,
            int quantity
    ) {
        return new OrderItem(
                UUID.randomUUID(),
                orderId,
                productId,
                productName,
                unitPrice,
                quantity,
                unitPrice.multiply(quantity)
        );
    }
}
