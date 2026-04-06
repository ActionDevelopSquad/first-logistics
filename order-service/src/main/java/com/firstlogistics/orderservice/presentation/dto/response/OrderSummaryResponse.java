package com.firstlogistics.orderservice.presentation.dto.response;

import com.firstlogistics.orderservice.application.dto.result.OrderSummaryResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSummaryResponse (
    UUID orderId,
    String status,
    Long totalAmount,
    UUID supplierCompanyId,
    UUID supplierManagerId,
    UUID receiverCompanyId,
    UUID receiverManagerId,
    UUID deliveryId,
    LocalDateTime dueDate,
    LocalDateTime orderedAt
) {
    public static OrderSummaryResponse from(OrderSummaryResult result) {
        return new OrderSummaryResponse(
                result.orderId(),
                result.status().name(),
                result.totalAmount(),
                result.supplierCompanyId(),
                result.supplierManagerId(),
                result.receiverCompanyId(),
                result.receiverManagerId(),
                result.deliveryId(),
                result.dueDate(),
                result.orderedAt()
        );
    }
}
