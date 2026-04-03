package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record ChangeDeliveryStatusCommand(
	UUID deliveryId,
	String role,
	UUID userId
) {
	public ChangeDeliveryStatusCommand {
		if (deliveryId == null || role == null || userId == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}
	}

	public static ChangeDeliveryStatusCommand of(UUID deliveryId, String role, UUID userId) {
		return new ChangeDeliveryStatusCommand(deliveryId, role, userId);
	}
}
