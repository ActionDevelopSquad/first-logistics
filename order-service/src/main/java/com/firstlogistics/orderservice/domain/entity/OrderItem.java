package com.firstlogistics.orderservice.domain.entity;

import com.firstlogistics.orderservice.domain.vo.Money;
import com.firstlogistics.orderservice.domain.vo.OrderId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    private UUID id;
    private OrderId orderId;
    private UUID productId;
    private String productName;
    private Money unitPrice;
    private int quantity;
    private Money subTotal;

    public static OrderItem create(
            OrderId orderId,
            UUID productId,
            String productName,
            Long unitPrice,
            int quantity
    ) {
        OrderItem item = new OrderItem();
        item.id = UUID.randomUUID();
        item.orderId = orderId;
        item.productId = productId;
        item.productName = productName;
        item.unitPrice = Money.of(unitPrice);
        item.quantity = quantity;
        item.calculateSubTotal();

        return item;
    }

    private void calculateSubTotal() {
        this.subTotal = unitPrice.multiply(quantity);
    }
}
