package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.StaffDetail;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryStaff {

	private DeliveryStaffId id;
	private StaffDetail staffDetail;
	private UUID hubId;
	private String slackId;
	private StaffType staffType;
	private int deliverySequence;
	private List<StaffTimetable> timetables;

	public static DeliveryStaff create(
		String staffName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		StaffType staffType,
		int deliverySequence
	) {
		if (hubId == null || staffType == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_STAFF_PARAMS);
		}
		if (slackId == null || slackId.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_STAFF_PARAMS);
		}
		return new DeliveryStaff(
			DeliveryStaffId.generate(), StaffDetail.of(staffName, phoneNumber), hubId, slackId,
			staffType, deliverySequence, new ArrayList<>()
		);
	}

	public static DeliveryStaff reconstitute(
		DeliveryStaffId id,
		StaffDetail staffDetail,
		UUID hubId,
		String slackId,
		StaffType staffType,
		int deliverySequence,
		List<StaffTimetable> timetables
	) {
		return new DeliveryStaff(id, staffDetail, hubId, slackId, staffType, deliverySequence, new ArrayList<>(timetables));
	}

	public void assignDelivery(DeliveryId deliveryId, LocalDateTime start, LocalDateTime end) {
		StaffTimetable timetable = StaffTimetable.create(this.id, deliveryId, start, end);
		this.timetables.add(timetable);
	}
}


