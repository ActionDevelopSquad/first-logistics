package com.firstlogistics.orderservice.domain.event;

import com.firstlogistics.orderservice.domain.entity.OrderItem;

import java.util.UUID;

public record OrderItemEventDto(
        UUID productId,
        String productName,
        Long unitPrice,
        Integer quantity
) {
    public static OrderItemEventDto from(OrderItem item) {
        return new OrderItemEventDto(
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice().amount(),
                item.getQuantity()
        );
    }
}