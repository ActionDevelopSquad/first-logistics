package com.firstlogistics.deliverservice.domain.event;

import java.util.UUID;

public record DeliveryUpdatedEvent(
	UUID deliveryId,
	UUID receiverId,
	String receiverSlackId
) {

	public static DeliveryUpdatedEvent create(UUID deliveryId, UUID receiverId, String receiverSlackId) {
		return new DeliveryUpdatedEvent(deliveryId, receiverId, receiverSlackId);
	}
}
