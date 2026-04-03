package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.Delivery;

import java.util.UUID;

public record UpdateDeliveryResult(
	UUID deliveryId
) {

	public static UpdateDeliveryResult from(Delivery delivery) {
		return new UpdateDeliveryResult(delivery.getId().id());
	}
}
