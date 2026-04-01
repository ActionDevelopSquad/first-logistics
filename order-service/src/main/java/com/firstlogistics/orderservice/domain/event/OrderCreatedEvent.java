package com.firstlogistics.orderservice.domain.event;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID supplierCompanyId,
        List<OrderItemEventDto> items
) {
}
