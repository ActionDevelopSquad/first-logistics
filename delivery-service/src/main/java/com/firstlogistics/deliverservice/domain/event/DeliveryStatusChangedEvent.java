package com.firstlogistics.deliverservice.domain.event;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryStatusChangedEvent(
	UUID deliveryId,
	UUID orderId,
	String status,
	UUID sourceHubId,
	UUID destinationHubId,
	String deliveryRoadAddress,
	String deliveryDetailAddress,
	UUID receiverId,
	String receiverSlackId,
	UUID receiverCompanyId,
	UUID currentHubId,
	List<RouteInfo> routes,
	LocalDateTime changedAt
) {

	public record RouteInfo(
		UUID routeId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		String status,
		UUID deliveryManagerId
	) {
		public static RouteInfo of(DeliveryRoute route) {
			return new RouteInfo(
				route.getId().id(),
				route.getDeliveryRouteSequence(),
				route.getSourceHubId(),
				route.getDestinationHubId(),
				route.getEstimatedDistance().meters(),
				route.getEstimatedDuration().minutes(),
				route.getStatus().name(),
				route.getDeliveryManagerId() != null ? route.getDeliveryManagerId().id() : null
			);
		}
	}

	public static DeliveryStatusChangedEvent create(Delivery delivery) {
		List<RouteInfo> routeInfos = delivery.getRoutes().stream()
			.map(RouteInfo::of)
			.toList();

		return new DeliveryStatusChangedEvent(
			delivery.getId().id(),
			delivery.getOrderId(),
			delivery.getStatus().name(),
			delivery.getSourceHubId(),
			delivery.getDestinationHubId(),
			delivery.getDeliveryAddress().roadAddress(),
			delivery.getDeliveryAddress().detailAddress(),
			delivery.getReceiverId(),
			delivery.getReceiverSlackId(),
			delivery.getReceiverCompanyId(),
			delivery.getCurrentHubId(),
			routeInfos,
			LocalDateTime.now()
		);
	}
}
