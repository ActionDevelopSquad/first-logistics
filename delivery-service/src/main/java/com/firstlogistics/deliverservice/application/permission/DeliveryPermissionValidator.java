package com.firstlogistics.deliverservice.application.permission;

import com.firstlogistics.deliverservice.application.permission.strategy.RolePermissionStrategy;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DeliveryPermissionValidator {

	private final Map<UserRole, RolePermissionStrategy> strategies;

	public DeliveryPermissionValidator(List<RolePermissionStrategy> strategyList) {
		this.strategies = strategyList.stream()
			.collect(Collectors.toMap(RolePermissionStrategy::supportedRole, strategy -> strategy));
	}

	public UserRole parseUserRole(String role) {
		try {
			return UserRole.valueOf(role);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new DeliveryException(DeliveryErrorCode.INVALID_ROLE_SCOPE);
		}
	}

	public void validate(DeliveryAccessContext context, String role, UUID userId, Set<UserRole> allowedRoles) {
		UserRole userRole = parseUserRole(role);
		if (!allowedRoles.contains(userRole)) {
			throw new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
		strategies.get(userRole).validate(context, userId);
	}
}
