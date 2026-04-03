package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.ChangeDeliveryStatusResult;

import java.util.UUID;

public record ChangeDeliveryStatusResponse(
	UUID deliveryId
) {
	public static ChangeDeliveryStatusResponse from(ChangeDeliveryStatusResult result) {
		return new ChangeDeliveryStatusResponse(result.deliveryId());
	}
}
