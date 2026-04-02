package com.firstlogistics.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderIdResponse(
        UUID orderId
) {
    public static OrderIdResponse from(UUID orderId) {
        return new OrderIdResponse(orderId);
    }
}
