package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryManagerCommand;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDeliveryManagerRequest(
	@NotNull UUID userId,
	@NotNull UUID hubId,
	@NotNull ManagerType managerType
) {
	public CreateDeliveryManagerCommand toCommand() {
		return new CreateDeliveryManagerCommand(userId, hubId, managerType);
	}
}
