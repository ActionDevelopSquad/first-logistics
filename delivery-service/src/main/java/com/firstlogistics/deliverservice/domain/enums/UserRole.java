package com.firstlogistics.deliverservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	MASTER(false),
	HUB_MANAGER(true),
	DELIVERY_MANAGER(true),
	COMPANY_MANAGER(true);

	private final boolean requiresScope;

	public static boolean isValid(String role) {
		try {
			valueOf(role);
			return true;
		} catch (IllegalArgumentException | NullPointerException e) {
			return false;
		}
	}
}
