package com.firstlogistics.deliverservice.domain.enums;

public enum DeliveryStatus {
	CREATED("배송 생성됨"),
	HUB_WAITING("허브 대기중"),
	FOR_HUB_MOVING("허브로 이동중"),
	HUB_ARRIVED("허브 도착"),
	FOR_COMPANY_MOVING("업체로 이동중"),
	COMPLETED("배송 완료"),
	CANCELLED("배송 취소");

	private final String description;

	DeliveryStatus(String description) {
		this.description = description;
	}
}
