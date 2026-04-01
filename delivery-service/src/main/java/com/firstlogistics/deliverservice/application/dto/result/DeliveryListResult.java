package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryListResult(
	List<DeliverySummary> deliveries,
	boolean hasNext,
	UUID nextCursorId,
	LocalDateTime nextCursorCreatedAt
) {

	public static DeliveryListResult of(List<DeliverySummary> deliveries, boolean hasNext) {
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
		UUID receiverCompanyId,
		UUID currentHubId,
		LocalDateTime createdAt
	) {
		public static DeliverySummary from(Delivery delivery, LocalDateTime createdAt) {
			return new DeliverySummary(
				delivery.getId().id(),
				delivery.getOrderId(),
				delivery.getStatus(),
				delivery.getSourceHubId(),
				delivery.getDestinationHubId(),
				delivery.getDeliveryAddress().roadAddress(),
				delivery.getReceiverCompanyId(),
				delivery.getCurrentHubId(),
				createdAt
			);
		}
	}
}
