package com.firstlogistics.orderservice.infrastructure.messaging.event;

import java.util.UUID;

public record InventoryReservationFailedEvent(
        UUID orderId,
        String reason
) {
}