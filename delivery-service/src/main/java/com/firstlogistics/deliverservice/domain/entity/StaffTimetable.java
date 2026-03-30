package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.StaffTimetableId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StaffTimetable {

	private StaffTimetableId id;
	private DeliveryStaffId staffId;
	private DeliveryId deliveryId;
	private LocalDateTime expectedStartAt;
	private LocalDateTime expectedEndAt;
	private TimetableStatus status;

	public static StaffTimetable create(
		DeliveryStaffId staffId,
		DeliveryId deliveryId,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt
	) {
		TimetableStatus createdStatus = TimetableStatus.CREATED;
		return new StaffTimetable(
			StaffTimetableId.generate(), staffId, deliveryId,
			expectedStartAt, expectedEndAt,
			createdStatus
		);
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
