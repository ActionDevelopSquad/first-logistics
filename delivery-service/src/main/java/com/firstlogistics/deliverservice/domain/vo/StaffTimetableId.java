package com.firstlogistics.deliverservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record StaffTimetableId(UUID id) {

	public StaffTimetableId {
		Objects.requireNonNull(id, "StaffTimetableId id must not be null");
	}

	public static StaffTimetableId of(UUID id) {
		return new StaffTimetableId(id);
	}

	public static StaffTimetableId generate() {
		return new StaffTimetableId(UUID.randomUUID());
	}
}
