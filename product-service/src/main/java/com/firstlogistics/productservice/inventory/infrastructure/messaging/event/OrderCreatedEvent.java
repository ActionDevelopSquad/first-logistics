package com.firstlogistics.productservice.inventory.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID supplierCompanyId,
        List<OrderItemInfo> orderItems
) {
    public record OrderItemInfo(UUID productId, Integer quantity) {}
}
