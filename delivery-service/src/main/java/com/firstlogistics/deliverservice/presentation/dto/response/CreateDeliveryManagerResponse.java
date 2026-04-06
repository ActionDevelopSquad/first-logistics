package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryManagerResult;

import java.util.UUID;

public record CreateDeliveryManagerResponse(
	UUID deliveryManagerId
) {
	public static CreateDeliveryManagerResponse from(CreateDeliveryManagerResult result) {
		return new CreateDeliveryManagerResponse(result.deliveryManagerId());
	}
}
