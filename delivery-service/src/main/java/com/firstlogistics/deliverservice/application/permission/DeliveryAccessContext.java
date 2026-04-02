package com.firstlogistics.deliverservice.application.permission;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.projection.DeliveryDetailProjection;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record DeliveryAccessContext(
	UUID sourceHubId,
	UUID destinationHubId,
	UUID receiverCompanyId,
	List<UUID> routeManagerIds
) {

	public static DeliveryAccessContext from(Delivery delivery) {
		return new DeliveryAccessContext(
			delivery.getSourceHubId(),
			delivery.getDestinationHubId(),
			delivery.getReceiverCompanyId(),
			delivery.getRoutes().stream()
				.map(route -> route.getDeliveryManagerId().id())
				.toList()
		);
	}

	public static DeliveryAccessContext from(
			DeliveryDetailProjection detail,
			List<DeliveryDetailProjection.RouteDetail> routes) {
		return new DeliveryAccessContext(
			detail.sourceHubId(),
			detail.destinationHubId(),
			detail.receiverCompanyId(),
			routes.stream()
				.map(DeliveryDetailProjection.RouteDetail::deliveryManagerId)
				.filter(Objects::nonNull)
				.toList()
		);
	}
}
