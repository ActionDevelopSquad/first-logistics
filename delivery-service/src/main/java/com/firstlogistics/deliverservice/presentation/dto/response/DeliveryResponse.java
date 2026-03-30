package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.DeliveryResult;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.util.UUID;

public record DeliveryResponse(
	UUID deliveryId,
	UUID orderId,
	DeliveryStatus status
) {
	public static DeliveryResponse from(DeliveryResult result) {
		return new DeliveryResponse(result.deliveryId(), result.orderId(), result.status());
	}
}
