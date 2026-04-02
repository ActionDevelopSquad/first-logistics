package com.firstlogistics.deliverservice.domain.projection;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.time.LocalDateTime;
import java.util.Objects;
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
	String companyManagerName,
	String companyManagerPhone,
	LocalDateTime createdAt
) {

	public DeliveryDetailProjection {
		Objects.requireNonNull(deliveryId, "deliveryId must not be null");
		Objects.requireNonNull(orderId, "orderId must not be null");
		Objects.requireNonNull(status, "status must not be null");
		Objects.requireNonNull(sourceHubId, "sourceHubId must not be null");
		Objects.requireNonNull(destinationHubId, "destinationHubId must not be null");
		Objects.requireNonNull(receiverId, "receiverId must not be null");
		Objects.requireNonNull(receiverCompanyId, "receiverCompanyId must not be null");
		Objects.requireNonNull(createdAt, "createdAt must not be null");
	}

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
		UUID deliveryManagerId,
		String managerName,
		String managerPhone,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt
	) {

		public RouteDetail {
			Objects.requireNonNull(routeId, "routeId must not be null");
			Objects.requireNonNull(sourceHubId, "sourceHubId must not be null");
			Objects.requireNonNull(destinationHubId, "destinationHubId must not be null");
			Objects.requireNonNull(status, "status must not be null");
		}
	}
}
