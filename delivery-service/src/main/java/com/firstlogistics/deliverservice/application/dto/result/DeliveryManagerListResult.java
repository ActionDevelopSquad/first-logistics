package com.firstlogistics.deliverservice.application.dto.result;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.projection.DeliveryManagerSummaryProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryManagerListResult(
	List<DeliveryManagerSummary> managers,
	boolean hasNext,
	UUID nextCursorId,
	LocalDateTime nextCursorCreatedAt
) {

	public record DeliveryManagerSummary(
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

		public static DeliveryManagerSummary from(DeliveryManagerSummaryProjection projection) {
			return new DeliveryManagerSummary(
				projection.managerId(),
				projection.userId(),
				projection.managerName(),
				projection.phoneNumber(),
				projection.hubId(),
				projection.slackId(),
				projection.managerType(),
				projection.deliverySequence(),
				projection.createdAt()
			);
		}
	}

	public static DeliveryManagerListResult from(List<DeliveryManagerSummaryProjection> projections, boolean hasNext) {
		List<DeliveryManagerSummary> summaries = projections.stream()
			.map(DeliveryManagerSummary::from)
			.toList();

		UUID nextCursorId = null;
		LocalDateTime nextCursorCreatedAt = null;
		if (hasNext && !summaries.isEmpty()) {
			DeliveryManagerSummary last = summaries.getLast();
			nextCursorId = last.managerId();
			nextCursorCreatedAt = last.createdAt();
		}

		return new DeliveryManagerListResult(summaries, hasNext, nextCursorId, nextCursorCreatedAt);
	}
}
