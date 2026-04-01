package com.firstlogistics.orderservice.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID supplierCompanyId,
        UUID supplierManagerId,
        UUID receiverCompanyId,
        UUID receiverManagerId,
        String roadAddress,
        String detailAddress,
        LocalDateTime dueDate,
        String requestMemo,
        List<OrderItemCommand> items
) {
    public record OrderItemCommand(
            UUID productId,
            String productName,
            Long unitPrice,
            Integer quantity
    ) {}
}