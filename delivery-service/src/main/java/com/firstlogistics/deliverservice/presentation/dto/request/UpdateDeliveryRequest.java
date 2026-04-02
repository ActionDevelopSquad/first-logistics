package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryCommand;

import java.util.UUID;

public record UpdateDeliveryRequest(
	UUID receiverId,
	String receiverSlackId
) {

	public UpdateDeliveryCommand toCommand(UUID deliveryId, String role, UUID userId) {
		return new UpdateDeliveryCommand(deliveryId, role, userId, receiverId, receiverSlackId);
	}
}
