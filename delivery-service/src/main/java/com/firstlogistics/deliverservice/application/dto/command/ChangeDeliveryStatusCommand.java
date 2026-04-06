package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record ChangeDeliveryStatusCommand(
	UUID deliveryId
) {
	public ChangeDeliveryStatusCommand {
		if (deliveryId == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}
	}

	public static ChangeDeliveryStatusCommand of(UUID deliveryId) {
		return new ChangeDeliveryStatusCommand(deliveryId);
	}
}
