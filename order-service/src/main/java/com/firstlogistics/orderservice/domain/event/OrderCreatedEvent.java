package com.firstlogistics.orderservice.domain.event;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderItem;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID supplierCompanyId,
        List<OrderItemInfo> orderItems
) {
    public record OrderItemInfo(
            UUID productId,
            Integer quantity
    ) {
        public static OrderItemInfo from(OrderItem orderItem) {
            return new OrderItemInfo(
                    orderItem.getProductId(),
                    orderItem.getQuantity()
            );
        }
    }

    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                order.getId().id(),
                order.getSupplier().companyId(),
                order.getOrderItems().stream()
                        .map(OrderItemInfo::from)
                        .toList()
        );
    }
}
