package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeliveryCreateRequest(
	@NotNull UUID orderId,
	@NotNull UUID sourceHubId,
	@NotNull UUID receiverCompanyId,
	@NotNull UUID receiverId,
	@NotNull String roadAddress,
	@NotNull String detailAddress,
	double latitude,
	double longitude
) {
	public CreateDeliveryCommand toCommand() {
		return new CreateDeliveryCommand(
			orderId, sourceHubId, receiverCompanyId, receiverId,
			roadAddress, detailAddress, latitude, longitude
		);
	}
}
