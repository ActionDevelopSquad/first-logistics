package com.firstlogistics.deliverservice.domain.event;

import java.util.UUID;

public record DeliveryCreatedEvent(UUID deliveryId, UUID orderId, String receiverSlackId) {

	public static DeliveryCreatedEvent create(UUID deliveryId, UUID orderId, String receiverSlackId) {
		return new DeliveryCreatedEvent(deliveryId, orderId, receiverSlackId);
	}
}
