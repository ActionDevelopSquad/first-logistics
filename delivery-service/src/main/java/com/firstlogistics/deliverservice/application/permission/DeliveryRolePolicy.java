package com.firstlogistics.deliverservice.application.permission;

import common.security.entity.enums.UserRole;

import java.util.Set;

public final class DeliveryRolePolicy {

	public static final Set<UserRole> ALL = Set.of(UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER, UserRole.COMPANY_MANAGER);
	public static final Set<UserRole> MANAGERS = Set.of(UserRole.MASTER, UserRole.HUB_MANAGER);
	public static final Set<UserRole> OPERATORS = Set.of(UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.DELIVERY_MANAGER);
	public static final Set<UserRole> MASTER_AND_DELIVERY = Set.of(UserRole.MASTER, UserRole.DELIVERY_MANAGER);
	public static final Set<UserRole> MASTER_ONLY = Set.of(UserRole.MASTER);

	public static boolean requiresScope(UserRole role) {
		return role != UserRole.MASTER;
	}

	private DeliveryRolePolicy() {
	}
}
