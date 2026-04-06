package com.firstlogistics.orderservice.domain.event;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderItem;

import java.util.List;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID orderId,
        UUID supplierCompanyId,
        List<OrderCancelledEvent.OrderItemInfo> orderItems,
        boolean isConfirmed
) {
    public record OrderItemInfo(
            UUID productId,
            Integer quantity
    ) {
        public static OrderCancelledEvent.OrderItemInfo from(OrderItem orderItem) {
            return new OrderCancelledEvent.OrderItemInfo(
                    orderItem.getProductId(),
                    orderItem.getQuantity()
            );
        }
    }

    public static OrderCancelledEvent of(Order order, boolean isConfirmed) {
        return new OrderCancelledEvent(
                order.getId().id(),
                order.getSupplier().companyId(),
                order.getOrderItems().stream()
                        .map(OrderCancelledEvent.OrderItemInfo::from)
                        .toList(),
                isConfirmed
        );
    }
}
