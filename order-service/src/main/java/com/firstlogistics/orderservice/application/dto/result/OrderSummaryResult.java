package com.firstlogistics.orderservice.application.dto.result;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.repository.dto.OrderSummaryDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSummaryResult(
        UUID orderId,
        OrderStatus status,
        Long totalAmount,
        UUID supplierCompanyId,
        UUID supplierManagerId,
        UUID receiverCompanyId,
        UUID receiverManagerId,
        UUID deliveryId,
        LocalDateTime dueDate,
        LocalDateTime orderedAt
) {
    public static OrderSummaryResult from(OrderSummaryDto dto) {
        return new OrderSummaryResult(
                dto.orderId(),
                dto.status(),
                dto.totalAmount(),
                dto.supplierCompanyId(),
                dto.supplierManagerId(),
                dto.receiverCompanyId(),
                dto.receiverManagerId(),
                dto.deliveryId(),
                dto.dueDate(),
                dto.orderedAt()
        );
    }
}
