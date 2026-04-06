package com.firstlogistics.deliverservice.presentation.dto.request;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryManagerListQuery;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerListRequest(
	ManagerType managerType,
	UUID hubId,
	String managerName,
	String phoneNumber,
	UUID cursorId,
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
	Integer size
) {

	public DeliveryManagerListQuery toQuery(String role, UUID userId) {
		return new DeliveryManagerListQuery(
			role,
			userId,
			managerType,
			hubId,
			managerName,
			phoneNumber,
			cursorId,
			cursorCreatedAt,
			size != null ? size : 10
		);
	}
}
