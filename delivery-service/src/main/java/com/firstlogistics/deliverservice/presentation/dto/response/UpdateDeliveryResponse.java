package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.UpdateDeliveryResult;

import java.util.UUID;

public record UpdateDeliveryResponse(
	UUID deliveryId
) {
	public static UpdateDeliveryResponse from(UpdateDeliveryResult result) {
		return new UpdateDeliveryResponse(result.deliveryId());
	}
}
