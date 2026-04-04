package com.firstlogistics.orderservice.domain.repository.dto;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSummaryDto(
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
) {}
