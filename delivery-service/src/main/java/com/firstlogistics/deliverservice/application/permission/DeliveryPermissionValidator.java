package com.firstlogistics.deliverservice.application.permission;

import com.firstlogistics.deliverservice.application.permission.strategy.RolePermissionStrategy;
import common.security.entity.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DeliveryPermissionValidator {

	private final Map<UserRole, RolePermissionStrategy> strategies;

	public DeliveryPermissionValidator(List<RolePermissionStrategy> strategyList) {
		this.strategies = strategyList.stream()
			.collect(Collectors.toMap(RolePermissionStrategy::supportedRole, strategy -> strategy));
	}

	public void validate(DeliveryAccessContext context, UserRole userRole, UUID userId) {
		RolePermissionStrategy strategy = strategies.get(userRole);
		if (strategy != null) {
			strategy.validate(context, userId);
		}
	}
}
