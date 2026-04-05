package com.firstlogistics.deliverservice.application.permission.strategy;

import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;

import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubManagerPermissionStrategy implements RolePermissionStrategy {

	private final HubManagerPort hubManagerPort;

	@Override
	public UserRole supportedRole() {
		return UserRole.HUB_MANAGER;
	}

	@Override
	public void validate(DeliveryAccessContext context, UUID userId) {
		UUID hubId = hubManagerPort.getHubManager(userId).hubId();
		if (!hubId.equals(context.sourceHubId()) && !hubId.equals(context.destinationHubId())) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}
}
