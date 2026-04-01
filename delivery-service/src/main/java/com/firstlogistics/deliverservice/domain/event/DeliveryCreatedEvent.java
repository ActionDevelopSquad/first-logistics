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
	) {
		public static OrderInfo of(UUID orderId, LocalDateTime orderedAt, LocalDateTime orderDueDate,
				String orderRequestNote, List<OrderItemInfo> orderItems) {
			return new OrderInfo(orderId, orderedAt, orderDueDate, orderRequestNote, orderItems);
		}
	}

	public record OrderItemInfo(
		UUID productId,
		String productName,
		int quantity,
		Long price
	) {
		public static OrderItemInfo of(UUID productId, String productName, int quantity, Long price) {
			return new OrderItemInfo(productId, productName, quantity, price);
		}
	}

	public record DeliveryInfo(
		UUID deliveryId,
		String receiverName,
		String receiverSlackId,
		String receiverRoadAddress,
		String receiverDetailAddress,
		List<DeliveryRouteInfo> deliveryRoutes,
		String companyStaffSlackId
	) {
		public static DeliveryInfo of(UUID deliveryId, String receiverName, String receiverSlackId,
				String receiverRoadAddress, String receiverDetailAddress,
				List<DeliveryRouteInfo> deliveryRoutes, String companyStaffSlackId) {
			return new DeliveryInfo(deliveryId, receiverName, receiverSlackId,
				receiverRoadAddress, receiverDetailAddress, deliveryRoutes, companyStaffSlackId);
		}
	}

	public record DeliveryRouteInfo(
		int sequence,
		UUID sourceHubId,
		UUID destinationHubId,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		String hubStaffSlackId
	) {
		public static DeliveryRouteInfo of(int sequence, UUID sourceHubId, UUID destinationHubId,
				int estimatedDistanceMeters, int estimatedDurationMinutes, String hubStaffSlackId) {
			return new DeliveryRouteInfo(sequence, sourceHubId, destinationHubId,
				estimatedDistanceMeters, estimatedDurationMinutes, hubStaffSlackId);
		}
	}
}
