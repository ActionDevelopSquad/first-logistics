package com.firstlogistics.deliverservice.infrastructure.messaging.producer.event;

import com.firstlogistics.deliverservice.domain.entity.Delivery;

import java.util.UUID;

public record DeliveryCreatedEvent(
	UUID deliveryId,
	UUID orderId,
	String receiverSlackId
) {
	public static DeliveryCreatedEvent create(UUID deliveryId, UUID orderId, String receiverSlackId) {
        return new DeliveryCreatedEvent(
                deliveryId, orderId, receiverSlackId);
    }
}
