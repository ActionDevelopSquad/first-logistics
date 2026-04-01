package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryCreateRequest(
	@NotNull UUID orderId,
	@NotNull LocalDateTime orderedAt,
	@NotNull LocalDateTime orderDueDate,
	String orderRequestNote,
	@NotNull @Valid SupplierInfo supplier,
	@NotNull @Valid ReceiverInfo receiver,
	@NotEmpty @Valid List<OrderItemInfo> orderItems
) {
	public record SupplierInfo(
		@NotNull UUID companyId,
		@NotNull UUID managerId
	) {}

	public record ReceiverInfo(
		@NotNull UUID companyId,
		@NotNull UUID managerId,
		@NotBlank String roadAddress,
		@NotBlank String detailAddress
	) {}

	public record OrderItemInfo(
		@NotNull UUID productId,
		@NotBlank String productName,
		@PositiveOrZero int quantity,
		@PositiveOrZero int price
	) {}

	public CreateDeliveryCommand toCommand() {
		return new CreateDeliveryCommand(
			orderId,
			orderedAt,
			orderDueDate,
			orderRequestNote,
			supplier.companyId(),
			supplier.managerId(),
			receiver.companyId(),
			receiver.managerId(),
			receiver.roadAddress(),
			receiver.detailAddress(),
			orderItems.stream()
				.map(i -> new CreateDeliveryCommand.OrderItemInfo(i.productId(), i.productName(), i.quantity(), i.price()))
				.toList()
		);
	}
}
