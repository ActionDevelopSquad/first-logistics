package com.firstlogistics.productservice.inventory.infrastructure.messaging.event;

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
    public record SupplierInfo(
            UUID companyId,
            UUID managerId
    ) {}

    public record ReceiverInfo(
            UUID companyId,
            UUID managerId,
            String roadAddress,
            String detailAddress
    ) {}

    public record OrderItemInfo(
            UUID productId,
            String productName,
            int quantity,
            Long price
    ) {}
}
