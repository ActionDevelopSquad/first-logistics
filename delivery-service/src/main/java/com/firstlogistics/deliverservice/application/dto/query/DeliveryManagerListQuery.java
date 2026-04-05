package com.firstlogistics.deliverservice.application.dto.query;

import com.firstlogistics.deliverservice.application.policy.PaginationPolicy;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerListQuery(
	String role,
	UUID userId,
	ManagerType managerType,
	UUID hubId,
	String managerName,
	String phoneNumber,
	UUID cursorId,
	LocalDateTime cursorCreatedAt,
	int size
) {

	public DeliveryManagerListQuery {
		size = PaginationPolicy.resolveSize(size);
	}

	public DeliveryManagerSearchSpec toSpec(DeliveryScope scope) {
		return DeliveryManagerSearchSpec.of(
			scope, managerType, hubId, managerName, phoneNumber,
			cursorId, cursorCreatedAt, size
		);
	}
}
