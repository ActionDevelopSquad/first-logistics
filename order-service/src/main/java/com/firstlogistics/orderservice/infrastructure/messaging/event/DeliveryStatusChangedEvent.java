package com.firstlogistics.orderservice.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryStatusChangedEvent(
        UUID orderId,
        DeliveryStatus status
) {

    public enum DeliveryStatus {
        CREATED,
        HUB_WAITING,
        FOR_HUB_MOVING,
        HUB_ARRIVED,
        FOR_COMPANY_MOVING,
        COMPLETED,
        CANCELLED
    }
}

