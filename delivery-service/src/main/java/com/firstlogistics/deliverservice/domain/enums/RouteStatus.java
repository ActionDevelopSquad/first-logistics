package com.firstlogistics.deliverservice.domain.enums;

public enum RouteStatus {
	CREATED("생성됨"),
	MOVING("이동중"),
	ARRIVED("도착"),
	COMPLETED("완료");

	private final String description;

	RouteStatus(String description) {
		this.description = description;
	}
}
