package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.StaffDetail;
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
		return new DeliveryStaff(id, staffDetail, hubId, slackId, staffType, deliverySequence, timetables);
	}

	public void addTimetable(StaffTimetable timetable) {
		this.timetables.add(timetable);
	}
}


