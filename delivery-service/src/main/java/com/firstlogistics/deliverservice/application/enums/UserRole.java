package com.firstlogistics.deliverservice.application.enums;

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
}
