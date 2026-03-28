package com.firstlogistics.deliverservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryErrorCode implements ErrorCode {

	// VO 유효성
	INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "DR001", "주소가 유효하지 않습니다."),
	INVALID_GEO_LOCATION(HttpStatus.BAD_REQUEST, "DR002", "위치 정보가 유효하지 않습니다."),
	INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "DR003", "거리 정보는 0 이상이어야 합니다."),
	INVALID_TIME(HttpStatus.BAD_REQUEST, "DR004", "시간 정보는 0 이상이어야 합니다."),
	INVALID_STAFF_DETAIL(HttpStatus.BAD_REQUEST, "DR005", "담당자 정보가 유효하지 않습니다."),

	// 배송
	DELIVERY_ALREADY_EXISTS(HttpStatus.CONFLICT, "DR101", "이미 배송이 존재합니다."),
	DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR102", "배송을 찾을 수 없습니다."),

	// 외부 서비스
	HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "DR401", "허브를 찾을 수 없습니다."),
	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR402", "업체를 찾을 수 없습니다."),
	EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DR403", "외부 서비스 호출에 실패했습니다.")

	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
