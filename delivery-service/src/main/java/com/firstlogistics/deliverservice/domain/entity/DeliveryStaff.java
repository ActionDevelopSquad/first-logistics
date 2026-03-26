package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.vo.StaffDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryStaff {

	private UUID id;
	private StaffDetail staffDetail;
	private UUID hubId;
	private String slackId;
	private StaffType staffType;
	private int deliverySequence;
	private Boolean isDelivering;

	private DeliveryStaff(
		UUID id,
		StaffDetail staffDetail,
		UUID hubId,
		String slackId,
		StaffType staffType,
		int deliverySequence,
		boolean isDelivering
	) {
		this.id = id;
		this.staffDetail = staffDetail;
		this.hubId = hubId;
		this.slackId = slackId;
		this.staffType = staffType;
		this.deliverySequence = deliverySequence;
		this.isDelivering = isDelivering;
	}

	public static DeliveryStaff create(
		UUID id,
		StaffDetail staffDetail,
		UUID hubId,
		String slackId,
		StaffType staffType,
		int deliverySequence
	) {
		boolean createdIsDelivering = false;
		return new DeliveryStaff(id, staffDetail, hubId, slackId, staffType, deliverySequence, createdIsDelivering);
	}

	public void startDelivering() {
		this.isDelivering = true;
	}

	public void finishDelivering() {
		this.isDelivering = false;
	}
}
