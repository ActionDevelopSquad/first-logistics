package com.firstlogistics.deliverservice.domain.enums;

public enum RouteStatus {
	CREATED("배송 생성됨"),
	HUB_WAITING("허브 대기중"),
	HUB_MOVING("허브 이동중"),
	DESTINATION_ARRIVED("허브 목적지 도착");

	private final String description;

	RouteStatus(String description) {
		this.description = description;
	}
}
