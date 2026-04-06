package com.firstlogistics.deliverservice.domain.spec;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerSearchSpec(
	DeliveryScope scope,
	ManagerType managerType,
	UUID hubId,
	String managerName,
	String phoneNumber,
	UUID cursorId,
	LocalDateTime cursorCreatedAt,
	int size
) {

	public DeliveryManagerSearchSpec {
		if (scope == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
		if (size <= 0) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
		if ((cursorId == null) != (cursorCreatedAt == null)) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_SEARCH_SPEC);
		}
	}

	public static DeliveryManagerSearchSpec of(
		DeliveryScope scope,
		ManagerType managerType,
		UUID hubId,
		String managerName,
		String phoneNumber,
		UUID cursorId,
		LocalDateTime cursorCreatedAt,
		int size
	) {
		return new DeliveryManagerSearchSpec(scope, managerType, hubId, managerName, phoneNumber, cursorId, cursorCreatedAt, size);
	}
}
