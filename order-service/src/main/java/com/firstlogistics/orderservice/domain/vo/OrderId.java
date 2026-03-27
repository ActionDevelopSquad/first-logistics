package com.firstlogistics.orderservice.domain.vo;

import java.util.UUID;

public record OrderId(
        UUID id
) {
    public static OrderId of() {
        return OrderId.of(UUID.randomUUID());
    }

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }
}
