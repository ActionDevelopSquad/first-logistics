package com.firstlogistics.notificationservice.ailog.presentation.dto.request;

import com.firstlogistics.notificationservice.ailog.application.dto.command.CreateAILogCommand;
import java.util.UUID;

public record AILogCreateRequest(
        UUID orderId,
        String customerName,
        String customerEmail,
        String orderTime,
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
                this.orderTime,
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