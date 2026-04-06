package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.entity.ManagerTimetable;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryManagerDetailResult(
	UUID managerId,
	UUID userId,
	String managerName,
	String phoneNumber,
	UUID hubId,
	String slackId,
	ManagerType managerType,
	int deliverySequence,
	List<TimetableInfo> timetables
) {

	public record TimetableInfo(
		UUID deliveryId,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt,
		TimetableStatus status
	) {

		public static TimetableInfo from(ManagerTimetable timetable) {
			return new TimetableInfo(
				timetable.getDeliveryId().id(),
				timetable.getExpectedStartAt(),
				timetable.getExpectedEndAt(),
				timetable.getStatus()
			);
		}
	}

	public static DeliveryManagerDetailResult from(DeliveryManager manager) {
		List<TimetableInfo> timetableInfos = manager.getTimetables().stream()
			.map(TimetableInfo::from)
			.toList();
		return new DeliveryManagerDetailResult(
			manager.getId().id(),
			manager.getUserId(),
			manager.getManagerDetail().managerName(),
			manager.getManagerDetail().phoneNumber(),
			manager.getHubId(),
			manager.getSlackId(),
			manager.getManagerType(),
			manager.getDeliverySequence(),
			timetableInfos
		);
	}
}
