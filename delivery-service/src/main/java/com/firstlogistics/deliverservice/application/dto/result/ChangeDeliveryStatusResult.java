package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.Delivery;

import java.util.UUID;

public record ChangeDeliveryStatusResult(
	UUID deliveryId
) {
	public static ChangeDeliveryStatusResult from(Delivery delivery) {
		return new ChangeDeliveryStatusResult(delivery.getId().id());
	}
}
