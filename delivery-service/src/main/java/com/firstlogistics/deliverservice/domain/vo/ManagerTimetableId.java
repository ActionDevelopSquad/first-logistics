package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record ManagerTimetableId(UUID id) {

	public ManagerTimetableId {
		if (id == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ID);
		}
	}

	public static ManagerTimetableId of(UUID id) {
		return new ManagerTimetableId(id);
	}

	public static ManagerTimetableId generate() {
		return new ManagerTimetableId(UUID.randomUUID());
	}
}
