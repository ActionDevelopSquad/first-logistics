package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.util.UUID;

public record DeliveryResult(
	UUID deliveryId,
	UUID orderId,
	DeliveryStatus status
) {

	public static DeliveryResult from(Delivery delivery) {
		return new DeliveryResult(
			delivery.getId().id(),
			delivery.getOrderId(),
			delivery.getStatus()
		);
	}
}
