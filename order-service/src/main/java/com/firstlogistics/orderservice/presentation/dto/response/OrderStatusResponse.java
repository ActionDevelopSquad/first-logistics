package com.firstlogistics.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderStatusResponse(
        UUID orderId,
        String status
) {
    public static OrderStatusResponse of(UUID userId, String status) {
        return new OrderStatusResponse(userId, status);
    }
}
