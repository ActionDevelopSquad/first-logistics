package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;

import java.util.UUID;

public record CreateDeliveryManagerResult(
	UUID deliveryManagerId
) {
	public static CreateDeliveryManagerResult from(DeliveryManager deliveryManager) {
		return new CreateDeliveryManagerResult(deliveryManager.getId().id());
	}
}
