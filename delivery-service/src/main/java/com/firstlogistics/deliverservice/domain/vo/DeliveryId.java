package com.firstlogistics.deliverservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record DeliveryId(UUID id) {

	public DeliveryId {
		Objects.requireNonNull(id, "DeliveryId value must not be null");
	}

	public static DeliveryId of(UUID id) {
		return new DeliveryId(id);
	}

	public static DeliveryId generate() {
		return new DeliveryId(UUID.randomUUID());
	}
}
