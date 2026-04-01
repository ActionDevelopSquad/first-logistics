package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateDeliveryResult(
	OrderInfo order,
	DeliveryInfo delivery
) {

	public static CreateDeliveryResult from(Delivery delivery, CreateDeliveryCommand command, String receiverName) {
		return new CreateDeliveryResult(
			new OrderInfo(
				delivery.getOrderId(),
				command.orderedAt(),
				command.orderDueDate(),
				command.orderRequestNote(),
				command.orderItems().stream()
					.map(i -> new OrderItemInfo(i.productId(), i.productName(), i.quantity(), i.price()))
					.toList()
			),
			new DeliveryInfo(
				delivery.getId().id(),
				delivery.getStatus(),
				delivery.getSourceHubId(),
				delivery.getDestinationHubId(),
				receiverName,
				delivery.getReceiverSlackId(),
				delivery.getDeliveryAddress().roadAddress(),
				delivery.getDeliveryAddress().detailAddress(),
				delivery.getCurrentHubId(),
				delivery.getRoutes().stream()
					.map(route -> new RouteInfo(
						route.getId().id(),
						route.getDeliveryRouteSequence(),
						route.getSourceHubId(),
						route.getDestinationHubId(),
						route.getEstimatedDistance().meters(),
						route.getEstimatedDuration().minutes(),
						route.getStatus()
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
