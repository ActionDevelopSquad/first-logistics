package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.projection.DeliverySummaryProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record DeliveryListResult(
	List<DeliverySummary> deliveries,
	boolean hasNext,
	UUID nextCursorId,
	LocalDateTime nextCursorCreatedAt
) {

	public DeliveryListResult {
		Objects.requireNonNull(deliveries, "deliveries must not be null");
	}

	public static DeliveryListResult from(List<DeliverySummaryProjection> projections, boolean hasNext) {
		List<DeliverySummary> deliveries = projections.stream()
			.map(DeliverySummary::from)
			.toList();
		if (!hasNext || deliveries.isEmpty()) {
			return new DeliveryListResult(deliveries, hasNext, null, null);
		}
		DeliverySummary last = deliveries.getLast();
		return new DeliveryListResult(deliveries, hasNext, last.deliveryId(), last.createdAt());
	}

	public record DeliverySummary(
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

		public DeliverySummary {
			Objects.requireNonNull(deliveryId, "deliveryId must not be null");
			Objects.requireNonNull(orderId, "orderId must not be null");
			Objects.requireNonNull(status, "status must not be null");
			Objects.requireNonNull(createdAt, "createdAt must not be null");
		}

		public static DeliverySummary from(DeliverySummaryProjection projection) {
			return new DeliverySummary(
				projection.deliveryId(),
				projection.orderId(),
				projection.status(),
				projection.sourceHubId(),
				projection.destinationHubId(),
				projection.receiverRoadAddress(),
				projection.receiverDetailAddress(),
				projection.receiverCompanyId(),
				projection.currentHubId(),
				projection.createdAt()
			);
		}
	}
}
