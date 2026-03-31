package com.firstlogistics.deliverservice.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryCreatedEvent(
	OrderInfo order,
	DeliveryInfo delivery
) {

	public static DeliveryCreatedEvent create(OrderInfo order, DeliveryInfo delivery) {
		return new DeliveryCreatedEvent(order, delivery);
	}

	public record OrderInfo(
		UUID orderId,
		LocalDateTime orderedAt,
		LocalDateTime orderDueDate,
		String orderRequestNote,
		List<OrderItemInfo> orderItems
	) {}

	public record OrderItemInfo(
		String productName,
		int quantity,
		int price
	) {}

	public record DeliveryInfo(
		UUID deliveryId,
		String receiverName,
		String receiverSlackId,
		String receiverRoadAddress,
		String receiverDetailAddress,
		List<DeliveryRouteInfo> deliveryRoutes,
		String companyStaffSlackId
	) {}

	public record DeliveryRouteInfo(
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		String hubStaffSlackId
	) {}
}
