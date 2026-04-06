package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryManagerCommand;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateDeliveryManagerRequest(
	@NotNull UUID hubId,
	@NotNull ManagerType managerType
) {

	public UpdateDeliveryManagerCommand toCommand(UUID managerId) {
		return new UpdateDeliveryManagerCommand(managerId, hubId, managerType);
	}
}
