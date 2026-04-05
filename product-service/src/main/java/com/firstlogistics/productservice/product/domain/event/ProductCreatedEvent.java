package com.firstlogistics.productservice.product.domain.event;

import java.util.UUID;

public record ProductCreatedEvent(UUID productId, int initialStock) {
}
