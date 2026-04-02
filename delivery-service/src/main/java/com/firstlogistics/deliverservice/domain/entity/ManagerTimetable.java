package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.ManagerTimetableId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ManagerTimetable {

	private ManagerTimetableId id;
	private DeliveryManagerId managerId;
	private DeliveryId deliveryId;
	private LocalDateTime expectedStartAt;
	private LocalDateTime expectedEndAt;
	private TimetableStatus status;

	public static ManagerTimetable create(
		DeliveryManagerId managerId,
		DeliveryId deliveryId,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt
	) {
		if (managerId == null || deliveryId == null || expectedStartAt == null || expectedEndAt == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_TIMETABLE_PARAMS);
		}
		TimetableStatus createdStatus = TimetableStatus.CREATED;
		return new ManagerTimetable(
			ManagerTimetableId.generate(), managerId, deliveryId,
			expectedStartAt, expectedEndAt,
			createdStatus
		);
	}

	public static ManagerTimetable reconstitute(
		ManagerTimetableId id,
		DeliveryManagerId managerId,
		DeliveryId deliveryId,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt,
		TimetableStatus status
	) {
		return new ManagerTimetable(id, managerId, deliveryId, expectedStartAt, expectedEndAt, status);
	}

	public void start() {
		this.status = TimetableStatus.HUB_MOVING;
	}

	public void complete() {
		this.status = TimetableStatus.COMPLETED;
	}

	public void cancel() {
		this.status = TimetableStatus.CANCELLED;
	}
}
