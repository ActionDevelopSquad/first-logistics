package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record CreateDeliveryManagerCommand(
	UUID userId,
	UUID hubId,
	ManagerType managerType
) {
	public CreateDeliveryManagerCommand {
		if (userId == null || hubId == null || managerType == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}
	}
}
