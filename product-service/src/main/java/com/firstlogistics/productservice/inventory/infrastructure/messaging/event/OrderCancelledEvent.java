package com.firstlogistics.productservice.inventory.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID orderId,
        UUID supplierCompanyId,
        List<OrderItemInfo> orderItems,
        boolean isConfirmed
) {
    public record OrderItemInfo(UUID productId, Integer quantity) {}
}
