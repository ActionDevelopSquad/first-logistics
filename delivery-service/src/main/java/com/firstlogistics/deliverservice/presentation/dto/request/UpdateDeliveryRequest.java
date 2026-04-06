package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryCommand;
import common.security.entity.enums.UserRole;

import java.util.UUID;

public record UpdateDeliveryRequest(
	UUID receiverId,
	String receiverSlackId
) {

	public UpdateDeliveryCommand toCommand(UUID deliveryId, UserRole role, UUID userId) {
		return new UpdateDeliveryCommand(deliveryId, role, userId, receiverId, receiverSlackId);
	}
}
