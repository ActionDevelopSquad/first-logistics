package com.firstlogistics.productservice.inventory.infrastructure.messaging.event;

import java.util.UUID;

public record InventoryReservedEvent(UUID orderId) {}
