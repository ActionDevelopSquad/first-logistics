package com.firstlogistics.deliverservice.domain.enums;

public enum StaffType {
	HUB_DELIVERY("허브 배송 담당자"),
	COMPANY_DELIVERY("업체 배송 담당자");

	private final String description;

	StaffType(String description) {
		this.description = description;
	}
}
