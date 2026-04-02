package com.firstlogistics.deliverservice.domain.vo;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record DeliveryRouteId(UUID id) {

	public DeliveryRouteId {
		if (id == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ID);
		}
	}

	public static DeliveryRouteId of(UUID id) {
		return new DeliveryRouteId(id);
	}

	public static DeliveryRouteId generate() {
		return new DeliveryRouteId(UUID.randomUUID());
	}
}
