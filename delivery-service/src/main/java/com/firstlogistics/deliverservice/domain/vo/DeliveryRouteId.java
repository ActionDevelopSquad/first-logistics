package com.firstlogistics.deliverservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record DeliveryRouteId(UUID id) {

	public DeliveryRouteId {
		Objects.requireNonNull(id, "DeliveryRouteId id must not be null");
	}

	public static DeliveryRouteId of(UUID id) {
		return new DeliveryRouteId(id);
	}

	public static DeliveryRouteId generate() {
		return new DeliveryRouteId(UUID.randomUUID());
	}
}
