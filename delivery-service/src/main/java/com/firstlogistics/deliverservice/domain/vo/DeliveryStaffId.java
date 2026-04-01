package com.firstlogistics.deliverservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record DeliveryStaffId(UUID id) {

	public DeliveryStaffId {
		Objects.requireNonNull(id, "DeliveryStaffId id must not be null");
	}

	public static DeliveryStaffId of(UUID id) {
		return new DeliveryStaffId(id);
	}

	public static DeliveryStaffId generate() {
		return new DeliveryStaffId(UUID.randomUUID());
	}
}
