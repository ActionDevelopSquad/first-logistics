package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.vo.StaffDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryStaff {

	private UUID id;
	private StaffDetail staffDetail;
	private UUID hubId;
	private String slackId;
	private StaffType staffType;
	private int deliverySequence;
	private boolean isDelivering;

	public static DeliveryStaff create(
		UUID id,
		String staffName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		StaffType staffType,
		int deliverySequence
	) {
		boolean createdIsDelivering = false;
		return new DeliveryStaff(
				id, StaffDetail.of(staffName, phoneNumber), hubId, slackId,
				staffType, deliverySequence, createdIsDelivering
		);
	}

	public void startDelivering() {
		this.isDelivering = true;
	}

	public void finishDelivering() {
		this.isDelivering = false;
	}
}
