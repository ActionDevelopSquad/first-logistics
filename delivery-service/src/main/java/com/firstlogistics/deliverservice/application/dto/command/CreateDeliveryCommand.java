package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.domain.event.OrderAcceptedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateDeliveryCommand(
	UUID orderId,
	LocalDateTime orderedAt,
	LocalDateTime orderDueDate,
	String orderRequestNote,
	UUID supplierCompanyId,
	UUID supplierManagerId,
	UUID receiverCompanyId,
	UUID receiverManagerId,
	String receiverRoadAddress,
	String receiverDetailAddress,
	List<OrderItemInfo> orderItems
) {
	public record OrderItemInfo(
		String productName,
		int quantity,
		int price
	) {}

	public static CreateDeliveryCommand from(OrderAcceptedEvent event) {
		return new CreateDeliveryCommand(
			event.orderId(),
			event.orderedAt(),
			event.orderDueDate(),
			event.orderRequestNote(),
			event.supplier().companyId(),
			event.supplier().managerId(),
			event.receiver().companyId(),
			event.receiver().managerId(),
			event.receiver().roadAddress(),
			event.receiver().detailAddress(),
			event.orderItems().stream()
				.map(i -> new OrderItemInfo(i.productName(), i.quantity(), i.price()))
				.toList()
		);
	}
}
