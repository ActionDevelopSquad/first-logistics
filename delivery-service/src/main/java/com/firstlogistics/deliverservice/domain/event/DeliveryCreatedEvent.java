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
		String companyDeliveryStaffSlackId,
		String companyDeliveryStaffName,
		String companyDeliveryStaffPhone,
		String companyDeliveryStaffEmail
	) {
		public static DeliveryInfo of(UUID deliveryId, String receiverName, String receiverSlackId,
				String receiverRoadAddress, String receiverDetailAddress,
				List<DeliveryRouteInfo> deliveryRoutes,
				String companyDeliveryStaffSlackId, String companyDeliveryStaffName,
				String companyDeliveryStaffPhone, String companyDeliveryStaffEmail) {
			return new DeliveryInfo(deliveryId, receiverName, receiverSlackId,
				receiverRoadAddress, receiverDetailAddress, deliveryRoutes,
				companyDeliveryStaffSlackId, companyDeliveryStaffName,
				companyDeliveryStaffPhone, companyDeliveryStaffEmail);
		}
	}

	public record DeliveryRouteInfo(
		int sequence,
		UUID sourceHubId,
		String sourceHubName,
		String sourceHubRoadAddress,
		UUID destinationHubId,
		String destinationHubName,
		String destinationHubRoadAddress,
		int estimatedDistanceMeters,
		int estimatedDurationMinutes,
		String hubDeliveryStaffSlackId
	) {
		public static DeliveryRouteInfo of(int sequence,
				UUID sourceHubId, String sourceHubName, String sourceHubRoadAddress,
				UUID destinationHubId, String destinationHubName, String destinationHubRoadAddress,
				int estimatedDistanceMeters, int estimatedDurationMinutes, String hubDeliveryStaffSlackId) {
			return new DeliveryRouteInfo(sequence,
				sourceHubId, sourceHubName, sourceHubRoadAddress,
				destinationHubId, destinationHubName, destinationHubRoadAddress,
				estimatedDistanceMeters, estimatedDurationMinutes, hubDeliveryStaffSlackId);
		}
	}
}
