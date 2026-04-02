package com.firstlogistics.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderStatusResponse(
        UUID orderId,
        String status
) {
    public static OrderStatusResponse of(UUID orderId, String status) {
        return new OrderStatusResponse(orderId, status);
    }
}
