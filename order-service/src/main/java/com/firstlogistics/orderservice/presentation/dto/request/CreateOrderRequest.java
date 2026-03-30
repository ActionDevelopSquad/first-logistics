package com.firstlogistics.orderservice.presentation.dto.request;

import com.firstlogistics.orderservice.application.dto.CreateOrderCommand;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull
        UUID supplierCompanyId,
        @NotNull
        UUID supplierManagerId,
        @NotNull
        UUID receiverCompanyId,
        @NotBlank
        String deliveryAddress,
        @FutureOrPresent
        LocalDateTime dueDate,
        String requestMemo,
        @NotEmpty
        List<OrderItemRequest> items
) {

    public CreateOrderCommand toCommand(UUID loginUserId) {
        return new CreateOrderCommand(
                this.supplierCompanyId,
                this.supplierManagerId,
                this.receiverCompanyId,
                loginUserId,
                this.deliveryAddress,
                this.dueDate,
                this.requestMemo,
                this.items.stream()
                        .map(OrderItemRequest::toCommand)
                        .toList()
        );
    }

    public record OrderItemRequest(
            @NotNull
            UUID productId,
            @NotBlank
            String productName,
            @Min(0)
            Long unitPrice,
            @Min(1)
            Integer quantity
    ) {
        public CreateOrderCommand.OrderItemCommand toCommand() {
            return new CreateOrderCommand.OrderItemCommand(
                    this.productId,
                    this.productName,
                    this.unitPrice,
                    this.quantity
            );
        }
    }
}