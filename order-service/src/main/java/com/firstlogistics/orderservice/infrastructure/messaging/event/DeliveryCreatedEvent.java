package com.firstlogistics.orderservice.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryCreatedEvent(
        OrderInfo order,
        DeliveryInfo delivery
) {
    public record OrderInfo(
            UUID orderId,
            UUID supplierCompanyId,
            List<OrderItemInfo> items
    ) {}

    public record OrderItemInfo(
            UUID productId,
            int quantity
    ) {}

    public record DeliveryInfo(UUID deliveryId) {}
}