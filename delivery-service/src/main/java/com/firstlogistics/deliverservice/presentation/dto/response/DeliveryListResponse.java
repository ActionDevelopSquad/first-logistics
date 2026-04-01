package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryListResponse(
	List<DeliverySummary> deliveries,
	boolean hasNext,
	UUID nextCursorId,
	LocalDateTime nextCursorCreatedAt
) {

	public static DeliveryListResponse from(DeliveryListResult result) {
		return new DeliveryListResponse(
			result.deliveries().stream()
				.map(DeliverySummary::from)
				.toList(),
			result.hasNext(),
			result.nextCursorId(),
			result.nextCursorCreatedAt()
		);
	}

	public record DeliverySummary(
		UUID deliveryId,
		UUID orderId,
		DeliveryStatus status,
		UUID sourceHubId,
		UUID destinationHubId,
		String receiverRoadAddress,
		UUID receiverCompanyId,
		UUID currentHubId,
		LocalDateTime createdAt
	) {
		public static DeliverySummary from(DeliveryListResult.DeliverySummary summary) {
			return new DeliverySummary(
				summary.deliveryId(),
				summary.orderId(),
				summary.status(),
				summary.sourceHubId(),
				summary.destinationHubId(),
				summary.receiverRoadAddress(),
				summary.receiverCompanyId(),
				summary.currentHubId(),
				summary.createdAt()
			);
		}
	}
}
