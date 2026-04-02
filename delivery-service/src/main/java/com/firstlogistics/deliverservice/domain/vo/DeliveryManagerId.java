package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record DeliveryManagerId(UUID id) {

	public DeliveryManagerId {
		if (id == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ID);
		}
	}

	public static DeliveryManagerId of(UUID id) {
		return new DeliveryManagerId(id);
	}

	public static DeliveryManagerId generate() {
		return new DeliveryManagerId(UUID.randomUUID());
	}
}
