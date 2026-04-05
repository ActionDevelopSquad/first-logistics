package com.firstlogistics.productservice.inventory.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

public record OrderAcceptedEvent(
        UUID orderId,
        List<OrderItemInfo> orderItems
) {
    public record OrderItemInfo(UUID productId, int quantity) {}
}
