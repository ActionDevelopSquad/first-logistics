package com.firstlogistics.deliverservice.application.permission.strategy;

import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;
import common.security.entity.enums.UserRole;

import java.util.UUID;

public interface RolePermissionStrategy {

	UserRole supportedRole();

	void validate(DeliveryAccessContext context, UUID userId);
}
