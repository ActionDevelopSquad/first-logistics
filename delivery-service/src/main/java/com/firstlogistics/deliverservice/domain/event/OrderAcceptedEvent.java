package com.firstlogistics.deliverservice.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 주문 승인 이벤트 (order.accepted 토픽)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderAcceptedEvent(
	UUID orderId,
	LocalDateTime orderedAt,
	LocalDateTime orderDueDate,
	String orderRequestNote,
	SupplierInfo supplier,
	ReceiverInfo receiver,
	List<OrderItemInfo> orderItems
) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SupplierInfo(
		UUID companyId,
		UUID managerId
	) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ReceiverInfo(
		UUID companyId,
		UUID managerId,
		String roadAddress,
		String detailAddress
	) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record OrderItemInfo(
		UUID productId,
		String productName,
		int quantity,
		Long price
	) {}
}
