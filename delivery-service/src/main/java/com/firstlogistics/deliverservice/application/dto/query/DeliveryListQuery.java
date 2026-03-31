package com.firstlogistics.deliverservice.application.dto.query;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryListQuery(
	DeliveryScope scope,

	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	UUID deliveryStaffId,

	LocalDateTime startDate,
	LocalDateTime endDate,

	UUID cursorId,
	LocalDateTime cursorCreatedAt,
	int size
) {

	private static final List<Integer> ALLOWED_SIZES = List.of(10, 30, 50);
	private static final int DEFAULT_SIZE = 10;

	public int resolvedSize() {
		return ALLOWED_SIZES.contains(size) ? size : DEFAULT_SIZE;
	}
}
