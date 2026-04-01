package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;

import java.util.UUID;

public record DeliveryDetail(
	UUID deliveryId,
	UUID orderId,
	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	String roadAddress,
	String detailAddress,
	UUID receiverId,
	UUID receiverCompanyId,
	UUID currentHubId,
	String companyStaffName,
	String companyStaffPhone
) {

	public record RouteDetail(
		UUID routeId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		int actualDistanceMeters,
		int actualDurationMinutes,
		RouteStatus status,
		UUID deliveryStaffId,
		String staffName,
		String staffPhone
	) {}
}
