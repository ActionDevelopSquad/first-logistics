package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record UpdateDeliveryCommand(
	UUID deliveryId,
	String role,
	UUID userId,
	UUID receiverId,
	String receiverSlackId
) {
	public UpdateDeliveryCommand {
		if (deliveryId == null || role == null || userId == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}
	}
}
