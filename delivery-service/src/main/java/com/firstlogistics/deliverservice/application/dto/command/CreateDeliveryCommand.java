package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.domain.event.OrderAcceptedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

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
	public CreateDeliveryCommand {
		if (orderId == null || supplierCompanyId == null || supplierManagerId == null
			|| receiverCompanyId == null || receiverManagerId == null
			|| receiverRoadAddress == null || receiverDetailAddress == null
			|| orderItems == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}
	}

	public record OrderItemInfo(
		UUID productId,
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
				.map(i -> new OrderItemInfo(i.productId(), i.productName(), i.quantity(), i.price()))
				.toList()
		);
	}
}
