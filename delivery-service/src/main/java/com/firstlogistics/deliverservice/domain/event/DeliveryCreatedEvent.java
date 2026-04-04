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
		UUID currentHubId,
		String receiverName,
		String receiverSlackId,
		String receiverEmail,
		String receiverPhone,
		String receiverRoadAddress,
		String receiverDetailAddress,
		List<DeliveryRouteInfo> deliveryRoutes,
		String companyDeliveryManagerSlackId,
		String companyDeliveryManagerName,
		String companyDeliveryManagerPhone,
		String companyDeliveryManagerEmail
	) {
		public static DeliveryInfo of(UUID deliveryId, UUID currentHubId, String receiverName, String receiverSlackId,
				String receiverEmail, String receiverPhone,
				String receiverRoadAddress, String receiverDetailAddress,
				List<DeliveryRouteInfo> deliveryRoutes,
				String companyDeliveryManagerSlackId, String companyDeliveryManagerName,
				String companyDeliveryManagerPhone, String companyDeliveryManagerEmail) {
			return new DeliveryInfo(deliveryId, currentHubId, receiverName, receiverSlackId,
				receiverEmail, receiverPhone,
				receiverRoadAddress, receiverDetailAddress, deliveryRoutes,
				companyDeliveryManagerSlackId, companyDeliveryManagerName,
				companyDeliveryManagerPhone, companyDeliveryManagerEmail);
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
		String hubDeliveryManagerSlackId
	) {
		public static DeliveryRouteInfo of(int sequence,
				UUID sourceHubId, String sourceHubName, String sourceHubRoadAddress,
				UUID destinationHubId, String destinationHubName, String destinationHubRoadAddress,
				int estimatedDistanceMeters, int estimatedDurationMinutes, String hubDeliveryManagerSlackId) {
			return new DeliveryRouteInfo(sequence,
				sourceHubId, sourceHubName, sourceHubRoadAddress,
				destinationHubId, destinationHubName, destinationHubRoadAddress,
				estimatedDistanceMeters, estimatedDurationMinutes, hubDeliveryManagerSlackId);
		}
	}
}
