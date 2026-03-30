package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.StaffDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
	private boolean isDelivering;

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
			staffType, deliverySequence, false
		);
	}

	public void startDelivering() {
		this.isDelivering = true;
	}

	public void finishDelivering() {
		this.isDelivering = false;
	}
}
