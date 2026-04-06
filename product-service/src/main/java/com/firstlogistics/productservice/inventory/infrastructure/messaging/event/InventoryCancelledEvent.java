package com.firstlogistics.productservice.inventory.infrastructure.messaging.event;

import java.util.UUID;

public record InventoryCancelledEvent(UUID orderId) {}
