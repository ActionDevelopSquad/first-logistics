package com.firstlogistics.deliverservice.domain.entity;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
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
	private UUID userId;
	private ManagerDetail managerDetail;
	private UUID hubId;
	private String slackId;
	private ManagerType managerType;
	private int deliverySequence;
	private List<ManagerTimetable> timetables;

	public static DeliveryManager create(
		UUID userId,
		String managerName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		ManagerType managerType,
		int deliverySequence
	) {
		if (userId == null || hubId == null || managerType == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
		if (slackId == null || slackId.isBlank()) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
		return new DeliveryManager(
			DeliveryManagerId.generate(), userId, ManagerDetail.of(managerName, phoneNumber), hubId, slackId,
			managerType, deliverySequence, new ArrayList<>()
		);
	}

	public static DeliveryManager reconstitute(
		DeliveryManagerId id,
		UUID userId,
		ManagerDetail managerDetail,
		UUID hubId,
		String slackId,
		ManagerType managerType,
		int deliverySequence,
		List<ManagerTimetable> timetables
	) {
		return new DeliveryManager(id, userId, managerDetail, hubId, slackId, managerType, deliverySequence, new ArrayList<>(timetables));
	}

	public void reassign(UUID hubId, ManagerType managerType) {
		if (hubId == null || managerType == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_MANAGER_PARAMS);
		}
		if (hasActiveDelivery()) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_MODIFIABLE);
		}
		this.hubId = hubId;
		this.managerType = managerType;
	}

	private boolean hasActiveDelivery() {
		return this.timetables.stream()
			.anyMatch(timetable -> timetable.getStatus() == TimetableStatus.CREATED
				|| timetable.getStatus() == TimetableStatus.HUB_MOVING);
	}

	public void validateDeletable() {
		if (hasActiveDelivery()) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_MODIFIABLE);
		}
	}

	public void assignDelivery(DeliveryId deliveryId, LocalDateTime start, LocalDateTime end) {
		ManagerTimetable timetable = ManagerTimetable.create(this.id, deliveryId, start, end);
		this.timetables.add(timetable);
	}
}


