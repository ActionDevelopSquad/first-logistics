package com.firstlogistics.orderservice.infrastructure.messaging.event;

import java.util.UUID;

public record InventoryResultEvent(
        UUID orderId
) {
}
