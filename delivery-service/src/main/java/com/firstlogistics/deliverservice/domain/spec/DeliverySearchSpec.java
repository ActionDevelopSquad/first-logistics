package com.firstlogistics.deliverservice.domain.spec;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliverySearchSpec(
	DeliveryScope scope,

	UUID orderId,
	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	UUID receiverCompanyId,
	UUID receiverId,
	List<UUID> resolvedReceiverIds,
	String managerName,
	String managerPhone,

	LocalDateTime startDate,
	LocalDateTime endDate,

	UUID cursorId,
	LocalDateTime cursorCreatedAt,
	int size
) {

	public DeliverySearchSpec {
		if (scope == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
		if (size <= 0) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
		if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
		if ((cursorId == null) != (cursorCreatedAt == null)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
	}

	public static DeliverySearchSpec of(
		DeliveryScope scope,
		UUID orderId, DeliveryStatus status,
		UUID sourceHubId, UUID destinationHubId,
		UUID receiverCompanyId, UUID receiverId, List<UUID> resolvedReceiverIds,
		String managerName, String managerPhone,
		LocalDateTime startDate, LocalDateTime endDate,
		UUID cursorId, LocalDateTime cursorCreatedAt, int size
	) {
		return new DeliverySearchSpec(
			scope,
			orderId, status,
			sourceHubId, destinationHubId,
			receiverCompanyId, receiverId, resolvedReceiverIds,
			managerName, managerPhone,
			startDate, endDate,
			cursorId, cursorCreatedAt, size
		);
	}
}
