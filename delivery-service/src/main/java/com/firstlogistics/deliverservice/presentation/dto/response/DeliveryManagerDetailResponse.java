package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerDetailResult;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryManagerDetailResponse(
	UUID managerId,
	UUID userId,
	String managerName,
	String phoneNumber,
	UUID hubId,
	String slackId,
	ManagerType managerType,
	int deliverySequence,
	List<TimetableResponse> timetables
) {

	public record TimetableResponse(
		UUID deliveryId,
		LocalDateTime expectedStartAt,
		LocalDateTime expectedEndAt,
		TimetableStatus status
	) {

		public static TimetableResponse from(DeliveryManagerDetailResult.TimetableInfo info) {
			return new TimetableResponse(
				info.deliveryId(),
				info.expectedStartAt(),
				info.expectedEndAt(),
				info.status()
			);
		}
	}

	public static DeliveryManagerDetailResponse from(DeliveryManagerDetailResult result) {
		List<TimetableResponse> timetableResponses = result.timetables().stream()
			.map(TimetableResponse::from)
			.toList();
		return new DeliveryManagerDetailResponse(
			result.managerId(),
			result.userId(),
			result.managerName(),
			result.phoneNumber(),
			result.hubId(),
			result.slackId(),
			result.managerType(),
			result.deliverySequence(),
			timetableResponses
		);
	}
}
