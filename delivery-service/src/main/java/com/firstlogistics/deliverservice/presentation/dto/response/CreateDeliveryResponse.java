package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;

import java.util.UUID;

public record CreateDeliveryResponse(
	UUID deliveryId
) {
	public static CreateDeliveryResponse from(CreateDeliveryResult result) {
		return new CreateDeliveryResponse(result.deliveryId());
	}
}
