package com.firstlogistics.deliverservice.infrastructure.messaging.producer.event;

import java.util.UUID;

public record DeliveryCreatedEvent(
	UUID deliveryId,
	UUID orderId,
	String receiverSlackId
) {
}
