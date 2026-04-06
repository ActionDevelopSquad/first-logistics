package com.firstlogistics.deliverservice.application.dto.query;

import com.firstlogistics.deliverservice.application.policy.PaginationPolicy;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;

import com.firstlogistics.common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

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
		try {
			UserRole.valueOf(role);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_QUERY_PARAMS);
		}
		size = PaginationPolicy.resolveSize(size);
	}

	public DeliveryManagerSearchSpec toSpec(DeliveryScope scope) {
		return DeliveryManagerSearchSpec.of(
			scope, managerType, hubId, managerName, phoneNumber,
			cursorId, cursorCreatedAt, size
		);
	}
}
