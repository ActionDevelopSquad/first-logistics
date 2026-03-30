package com.firstlogistics.deliverservice.application.dto.command;

import java.util.UUID;

public record CreateDeliveryCommand(
	UUID orderId,
	UUID sourceHubId,
	UUID receiverCompanyId,
	UUID receiverId,
	String roadAddress,
	String detailAddress,
	double latitude,
	double longitude
) {
}
