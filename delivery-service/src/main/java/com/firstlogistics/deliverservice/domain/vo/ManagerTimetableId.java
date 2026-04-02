package com.firstlogistics.deliverservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record ManagerTimetableId(UUID id) {

	public ManagerTimetableId {
		Objects.requireNonNull(id, "ManagerTimetableId id must not be null");
	}

	public static ManagerTimetableId of(UUID id) {
		return new ManagerTimetableId(id);
	}

	public static ManagerTimetableId generate() {
		return new ManagerTimetableId(UUID.randomUUID());
	}
}
