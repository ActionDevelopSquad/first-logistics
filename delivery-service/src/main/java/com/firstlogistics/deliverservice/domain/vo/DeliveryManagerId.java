package com.firstlogistics.deliverservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record DeliveryManagerId(UUID id) {

	public DeliveryManagerId {
		Objects.requireNonNull(id, "DeliveryManagerId id must not be null");
	}

	public static DeliveryManagerId of(UUID id) {
		return new DeliveryManagerId(id);
	}

	public static DeliveryManagerId generate() {
		return new DeliveryManagerId(UUID.randomUUID());
	}
}
