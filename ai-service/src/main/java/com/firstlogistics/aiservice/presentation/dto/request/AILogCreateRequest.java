package com.firstlogistics.aiservice.presentation.dto.request;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;

import java.time.LocalDateTime;
import java.util.UUID;

public record AILogCreateRequest(
        UUID orderId,
        String customerName,
        String customerEmail,
        LocalDateTime orderedAt,
        LocalDateTime orderDueDate,
        String productInfo,
        String requirement,
        String departure,
        String hubs,
        String destination,
        String deliveryStaffName,
        String deliveryStaffEmail,
        String hubManagerSlackId
) {
    public CreateAILogCommand toCommand() {
        return new CreateAILogCommand(
                this.orderId,
                this.customerName,
                this.customerEmail,
                this.orderedAt,
                this.orderDueDate,
                this.productInfo,
                this.requirement,
                this.departure,
                this.hubs,
                this.destination,
                this.deliveryStaffName,
                this.deliveryStaffEmail,
                this.hubManagerSlackId
        );
    }
}