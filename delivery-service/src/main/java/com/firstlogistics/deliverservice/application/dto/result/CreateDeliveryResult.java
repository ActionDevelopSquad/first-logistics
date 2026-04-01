package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.Delivery;

import java.util.UUID;

public record CreateDeliveryResult(
	UUID deliveryId
) {

	public static CreateDeliveryResult from(Delivery delivery) {
		return new CreateDeliveryResult(delivery.getId().id());
	}
}
