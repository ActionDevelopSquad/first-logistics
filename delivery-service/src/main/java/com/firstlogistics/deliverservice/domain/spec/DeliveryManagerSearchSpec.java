package com.firstlogistics.deliverservice.domain.spec;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;

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
