package com.firstlogistics.orderservice.application.dto.result;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderItem;
import com.firstlogistics.orderservice.domain.enums.OrderCancelType;
import com.firstlogistics.orderservice.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResult(
        UUID orderId,
        OrderStatus status,
        Long totalAmount,
        LocalDateTime orderedAt,
        LocalDateTime dueDate,

        UUID supplierCompanyId,
        UUID supplierManagerId,
        UUID receiverCompanyId,
        UUID receiverManagerId,

        UUID deliveryId,
        String roadAddress,
        String detailAddress,
        String requestMemo,

        OrderCancelType cancelType,

        List<OrderItemResult> orderItems
) {
    public static OrderDetailResult from(Order order) {
        return new OrderDetailResult(
                order.getId().id(),
                order.getStatus(),
                order.getTotalAmount().amount(),
                order.getOrderedAt(),
                order.getDueDate(),

                order.getSupplier().companyId(),
                order.getSupplier().managerId(),

                order.getReceiver().companyId(),
                order.getReceiver().managerId(),

                order.getDeliveryId(),

                order.getDeliveryAddress().roadAddress(),
                order.getDeliveryAddress().detailAddress(),

                order.getRequestMemo(),
                order.getCancelType(),

                order.getOrderItems().stream()
                        .map(OrderItemResult::from)
                        .toList()
        );
    }

    public record OrderItemResult(
            UUID productId,
            String productName,
            Long unitPrice,
            Integer quantity,
            Long subTotal
    ) {
        public static OrderItemResult from(OrderItem item) {
            return new OrderItemResult(
                    item.getProductId(),
                    item.getProductName(),
                    item.getUnitPrice().amount(),
                    item.getQuantity(),
                    item.getSubTotal().amount()
            );
        }
    }
}