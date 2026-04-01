package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryListRequest(
	UUID orderId,
	DeliveryStatus status,
	UUID sourceHubId,
	UUID destinationHubId,
	UUID receiverCompanyId,
	String receiverName,
	String receiverPhone,
	UUID receiverId,
	String staffName,
	String staffPhone,
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
	UUID cursorId,
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
	Integer size
) {

	public DeliveryListQuery toQuery(String role, UUID userId) {
		return new DeliveryListQuery(
			role,
			userId,
			null,
			orderId,
			status,
			sourceHubId,
			destinationHubId,
			receiverCompanyId,
			receiverId,
			null,
			receiverName,
			receiverPhone,
			staffName,
			staffPhone,
			startDate,
			endDate,
			cursorId,
			cursorCreatedAt,
			size != null ? size : 10
		);
	}
}
