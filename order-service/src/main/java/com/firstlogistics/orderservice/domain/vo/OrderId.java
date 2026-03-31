package com.firstlogistics.orderservice.domain.vo;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;

import java.util.UUID;

public record OrderId(
        UUID id
) {
    public OrderId {
        if (id == null) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_ID);
        }
    }
    public static OrderId of() {
        return OrderId.of(UUID.randomUUID());
    }

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }
}
