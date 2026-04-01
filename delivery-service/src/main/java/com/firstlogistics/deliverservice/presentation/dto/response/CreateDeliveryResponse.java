package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateDeliveryResponse(
	OrderInfo order,
	DeliveryInfo delivery
) {
	public static CreateDeliveryResponse from(CreateDeliveryResult result) {
		return new CreateDeliveryResponse(
			new OrderInfo(
				result.order().orderId(),
				result.order().orderedAt(),
				result.order().orderDueDate(),
				result.order().orderRequestNote(),
				result.order().orderItems().stream()
					.map(item -> new OrderItemInfo(item.productId(), item.productName(), item.quantity(), item.price()))
					.toList()
			),
			new DeliveryInfo(
				result.delivery().deliveryId(),
				result.delivery().status(),
				result.delivery().sourceHubId(),
				result.delivery().destinationHubId(),
				result.delivery().receiverName(),
				result.delivery().receiverSlackId(),
				result.delivery().receiverRoadAddress(),
				result.delivery().receiverDetailAddress(),
				result.delivery().currentHubId(),
				result.delivery().routes().stream()
					.map(route -> new RouteInfo(
						route.routeId(),
						route.sequence(),
						route.sourceHubId(),
						route.destinationHubId(),
						route.estimatedDistanceMeters(),
						route.estimatedDurationMinutes(),
						route.status()
					))
					.toList()
			)
		);
	}

	public record OrderInfo(
		UUID orderId,
		LocalDateTime orderedAt,
		LocalDateTime orderDueDate,
		String orderRequestNote,
		List<OrderItemInfo> orderItems
	) {}

	public record OrderItemInfo(
		UUID productId,
		String productName,
		int quantity,
		Long price
	) {}

	public record DeliveryInfo(
		UUID deliveryId,
		DeliveryStatus status,
		UUID sourceHubId,
		UUID destinationHubId,
		String receiverName,
		String receiverSlackId,
		String receiverRoadAddress,
		String receiverDetailAddress,
		UUID currentHubId,
		List<RouteInfo> routes
	) {}

	public record RouteInfo(
		UUID routeId,
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		RouteStatus status
	) {}
}
