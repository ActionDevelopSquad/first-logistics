package com.firstlogistics.deliverservice.presentation.dto.response;

import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerListResult;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryManagerListResponse(
	List<DeliveryManagerSummaryResponse> managers,
	boolean hasNext,
	UUID nextCursorId,
	LocalDateTime nextCursorCreatedAt
) {

	public record DeliveryManagerSummaryResponse(
		UUID managerId,
		UUID userId,
		String managerName,
		String phoneNumber,
		UUID hubId,
		String slackId,
		ManagerType managerType,
		int deliverySequence,
		LocalDateTime createdAt
	) {

		public static DeliveryManagerSummaryResponse from(DeliveryManagerListResult.DeliveryManagerSummary summary) {
			return new DeliveryManagerSummaryResponse(
				summary.managerId(),
				summary.userId(),
				summary.managerName(),
				summary.phoneNumber(),
				summary.hubId(),
				summary.slackId(),
				summary.managerType(),
				summary.deliverySequence(),
				summary.createdAt()
			);
		}
	}

	public static DeliveryManagerListResponse from(DeliveryManagerListResult result) {
		List<DeliveryManagerSummaryResponse> responses = result.managers().stream()
			.map(DeliveryManagerSummaryResponse::from)
			.toList();
		return new DeliveryManagerListResponse(responses, result.hasNext(),
			result.nextCursorId(), result.nextCursorCreatedAt());
	}
}
