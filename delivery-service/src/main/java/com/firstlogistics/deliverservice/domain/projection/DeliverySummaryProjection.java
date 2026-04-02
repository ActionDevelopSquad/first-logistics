package com.firstlogistics.deliverservice.domain.projection;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record DeliverySummaryProjection(
	UUID deliveryId,
	UUID orderId,
	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	String receiverRoadAddress,
	String receiverDetailAddress,
	UUID receiverCompanyId,
	UUID currentHubId,
	LocalDateTime createdAt
) {

	public DeliverySummaryProjection {
		Objects.requireNonNull(deliveryId, "deliveryId must not be null");
		Objects.requireNonNull(orderId, "orderId must not be null");
		Objects.requireNonNull(status, "status must not be null");
		Objects.requireNonNull(sourceHubId, "sourceHubId must not be null");
		Objects.requireNonNull(destinationHubId, "destinationHubId must not be null");
		Objects.requireNonNull(createdAt, "createdAt must not be null");
	}
}
