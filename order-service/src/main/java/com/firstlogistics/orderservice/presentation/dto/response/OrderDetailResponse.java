package com.firstlogistics.orderservice.presentation.dto.response;

import com.firstlogistics.orderservice.application.dto.result.OrderDetailResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        UUID orderId,
        String status,
        Long totalAmount,
        LocalDateTime orderedAt,
        LocalDateTime dueDate,

        SupplierInfo supplier,
        ReceiverInfo receiver,

        UUID deliveryId,
        String requestMemo,

        String cancelType,

        List<OrderItemResponse> items
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

    public record OrderItemResponse(
            UUID productId,
            String productName,
            Long unitPrice,
            Integer quantity,
            Long subTotal
    ) {}

    public static OrderDetailResponse from(OrderDetailResult result) {
        return new OrderDetailResponse(
                result.orderId(),
                result.status().name(),
                result.totalAmount(),
                result.orderedAt(),
                result.dueDate(),
                new SupplierInfo(result.supplierCompanyId(), result.supplierManagerId()),
                new ReceiverInfo(result.receiverCompanyId(), result.receiverManagerId(), result.roadAddress(), result.detailAddress()),
                result.deliveryId(),
                result.requestMemo(),
                result.cancelType() != null ? result.cancelType().name() : null,
                result.orderItems().stream()
                        .map(i -> new OrderItemResponse(
                                i.productId(),
                                i.productName(),
                                i.unitPrice(),
                                i.quantity(),
                                i.subTotal()
                                )
                        )
                        .toList()
        );
    }
}