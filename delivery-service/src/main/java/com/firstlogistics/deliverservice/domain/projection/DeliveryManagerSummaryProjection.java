package com.firstlogistics.deliverservice.domain.projection;

import com.firstlogistics.deliverservice.domain.enums.ManagerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerSummaryProjection(
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
}
