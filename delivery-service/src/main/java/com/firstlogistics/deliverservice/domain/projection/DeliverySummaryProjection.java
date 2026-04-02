package com.firstlogistics.deliverservice.domain.projection;

import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliverySummaryProjection(
	UUID deliveryId,
	UUID orderId,
	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	String receiverRoadAddress,
	String receiverDetailAddress,
	UUID receiverCompanyId,
	UUID currentHubId,
	LocalDateTime createdAt
) {}
