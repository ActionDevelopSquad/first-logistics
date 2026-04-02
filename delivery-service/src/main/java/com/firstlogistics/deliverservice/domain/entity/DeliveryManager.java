package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.ManagerDetail;

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
public class DeliveryManager {

	private DeliveryManagerId id;
	private ManagerDetail managerDetail;
	private UUID hubId;
	private String slackId;
	private ManagerType managerType;
	private int deliverySequence;
	private List<ManagerTimetable> timetables;

	public static DeliveryManager create(
		String managerName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		ManagerType managerType,
		int deliverySequence
	) {
		if (hubId == null || managerType == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
		if (slackId == null || slackId.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
		return new DeliveryManager(
			DeliveryManagerId.generate(), ManagerDetail.of(managerName, phoneNumber), hubId, slackId,
			managerType, deliverySequence, new ArrayList<>()
		);
	}

	public static DeliveryManager reconstitute(
		DeliveryManagerId id,
		ManagerDetail managerDetail,
		UUID hubId,
		String slackId,
		ManagerType managerType,
		int deliverySequence,
		List<ManagerTimetable> timetables
	) {
		return new DeliveryManager(id, managerDetail, hubId, slackId, managerType, deliverySequence, new ArrayList<>(timetables));
	}

	public void assignDelivery(DeliveryId deliveryId, LocalDateTime start, LocalDateTime end) {
		ManagerTimetable timetable = ManagerTimetable.create(this.id, deliveryId, start, end);
		this.timetables.add(timetable);
	}
}


