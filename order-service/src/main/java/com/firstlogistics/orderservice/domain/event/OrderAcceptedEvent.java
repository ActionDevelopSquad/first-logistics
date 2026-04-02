package com.firstlogistics.orderservice.domain.event;

import com.firstlogistics.orderservice.domain.entity.Order;
import com.firstlogistics.orderservice.domain.entity.OrderItem;
import com.firstlogistics.orderservice.domain.vo.Address;
import com.firstlogistics.orderservice.domain.vo.Receiver;
import com.firstlogistics.orderservice.domain.vo.Supplier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderAcceptedEvent(
        UUID orderId,
        LocalDateTime orderedAt,
        LocalDateTime orderDueDate,
        String orderRequestNote,
        SupplierInfo supplier,
        ReceiverInfo receiver,
        List<OrderItemInfo> orderItems
) {
    public static OrderAcceptedEvent from(Order order) {
        return new OrderAcceptedEvent(
                order.getId().id(),
                order.getOrderedAt(),
                order.getDueDate(),
                order.getRequestMemo(),
                SupplierInfo.from(order.getSupplier()),
                ReceiverInfo.from(order.getReceiver(), order.getDeliveryAddress()),
                order.getOrderItems().stream()
                        .map(OrderItemInfo::from)
                        .toList()
        );
    }

    public record SupplierInfo(
            UUID companyId,
            UUID managerId
    ) {
        public static SupplierInfo from(Supplier supplier) {
            return new SupplierInfo(supplier.companyId(), supplier.managerId());
        }
    }

    public record ReceiverInfo(
            UUID companyId,
            UUID managerId,
            String roadAddress,
            String detailAddress
    ) {
        public static ReceiverInfo from(Receiver receiver, Address address) {
            return new ReceiverInfo(receiver.companyId(), receiver.managerId(),
                    address.roadAddress(), address.detailAddress());
        }
    }

    public record OrderItemInfo(
            UUID productId,
            String productName,
            int quantity,
            Long price
    ) {
        public static OrderItemInfo from(OrderItem item) {
            return new OrderItemInfo(item.getProductId(), item.getProductName(),
                    item.getQuantity(), item.getUnitPrice().amount());
        }
    }
}