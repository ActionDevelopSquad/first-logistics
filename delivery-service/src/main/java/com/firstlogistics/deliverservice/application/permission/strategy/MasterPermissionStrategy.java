package com.firstlogistics.deliverservice.application.permission.strategy;

import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;

import common.security.entity.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MasterPermissionStrategy implements RolePermissionStrategy {

	@Override
	public UserRole supportedRole() {
		return UserRole.MASTER;
	}

	@Override
	public void validate(DeliveryAccessContext context, UUID userId) {
	}
}
