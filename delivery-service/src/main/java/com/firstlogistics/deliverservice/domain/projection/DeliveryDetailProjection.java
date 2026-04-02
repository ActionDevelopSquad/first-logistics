package com.firstlogistics.deliverservice.domain.projection;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryDetailProjection(
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
	String companyStaffPhone,
	LocalDateTime createdAt
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
		String staffPhone,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt
	) {}
}
