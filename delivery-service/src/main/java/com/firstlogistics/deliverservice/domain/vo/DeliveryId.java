package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record DeliveryId(UUID id) {

	public DeliveryId {
		if (id == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ID);
		}
	}

	public static DeliveryId of(UUID id) {
		return new DeliveryId(id);
	}

	public static DeliveryId generate() {
		return new DeliveryId(UUID.randomUUID());
	}
}
