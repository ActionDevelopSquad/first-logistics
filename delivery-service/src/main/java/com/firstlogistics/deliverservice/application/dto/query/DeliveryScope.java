package com.firstlogistics.deliverservice.application.dto.query;

import com.firstlogistics.deliverservice.application.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;

import java.util.UUID;

public record DeliveryScope(UserRole role, UUID scopeId) {

	public DeliveryScope {
		if (role.isRequiresScope() && scopeId == null) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ROLE_SCOPE);
		}
	}

	public static DeliveryScope of(UserRole role, UUID scopeId) {
		return new DeliveryScope(role, scopeId);
	}

	public static DeliveryScope from(String role, UUID userId, UUID hubId, UUID companyId) {
		UserRole userRole = UserRole.valueOf(role);
		UUID scopeId = switch (userRole) {
			case HUB_MANAGER -> hubId;
			case DELIVERY_MANAGER -> userId;
			case COMPANY_MANAGER -> companyId;
			default -> null;
		};
		return DeliveryScope.of(userRole, scopeId);
	}
}
