package com.firstlogistics.deliverservice.domain.enums;

public enum DeliveryStatus {
	CREATED("배송 생성됨"),
	HUB_WAITING("허브 대기중"),
	HUB_MOVING("허브 이동중"),
	DESTINATION_ARRIVED("목적지 도착"),
	FOR_VENDOR_MOVING("업체로 이동중"),
	COMPLETED("배송 완료"),
	CANCELLED("배송 취소"),
	CANCEL_REQUESTED("배송 취소 대기");

	private final String description;

	DeliveryStatus(String description) {
		this.description = description;
	}
}
