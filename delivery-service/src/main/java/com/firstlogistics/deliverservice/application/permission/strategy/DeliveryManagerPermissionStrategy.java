package com.firstlogistics.deliverservice.application.permission.strategy;

import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;

import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryManagerPermissionStrategy implements RolePermissionStrategy {

	private final DeliveryManagerRepository deliveryManagerRepository;

	@Override
	public UserRole supportedRole() {
		return UserRole.DELIVERY_MANAGER;
	}

	@Override
	public void validate(DeliveryAccessContext context, UUID userId) {
		UUID managerId = deliveryManagerRepository.findByUserId(userId)
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND))
			.getId().id();
		boolean isAssigned = context.routeManagerIds().contains(managerId);
		if (!isAssigned) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}
}
