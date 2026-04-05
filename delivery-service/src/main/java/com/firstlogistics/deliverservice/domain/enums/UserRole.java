package com.firstlogistics.deliverservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	MASTER(false),
	HUB_MANAGER(true),
	DELIVERY_MANAGER(true),
	COMPANY_MANAGER(true);

	private final boolean requiresScope;

	public static final Set<UserRole> ALL = Set.of(MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER);
	public static final Set<UserRole> MANAGERS = Set.of(MASTER, HUB_MANAGER);
	public static final Set<UserRole> OPERATORS = Set.of(MASTER, HUB_MANAGER, DELIVERY_MANAGER);
	public static final Set<UserRole> MASTER_AND_DELIVERY = Set.of(MASTER, DELIVERY_MANAGER);
	public static final Set<UserRole> MASTER_ONLY = Set.of(MASTER);

	public static boolean isValid(String role) {
		try {
			valueOf(role);
			return true;
		} catch (IllegalArgumentException | NullPointerException e) {
			return false;
		}
	}
}
