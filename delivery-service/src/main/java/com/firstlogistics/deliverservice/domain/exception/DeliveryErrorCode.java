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

	// 엔티티 유효성
	INVALID_DELIVERY_PARAMS(HttpStatus.BAD_REQUEST, "DR006", "배송 생성 파라미터가 유효하지 않습니다."),
	INVALID_DELIVERY_ROUTE_PARAMS(HttpStatus.BAD_REQUEST, "DR007", "배송 경로 생성 파라미터가 유효하지 않습니다."),
	INVALID_DELIVERY_STAFF_PARAMS(HttpStatus.BAD_REQUEST, "DR008", "배송 담당자 생성 파라미터가 유효하지 않습니다."),
	INVALID_TIMETABLE_PARAMS(HttpStatus.BAD_REQUEST, "DR009", "타임테이블 생성 파라미터가 유효하지 않습니다."),

	// 배송
	DELIVERY_ALREADY_EXISTS(HttpStatus.CONFLICT, "DR101", "이미 배송이 존재합니다."),
	DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR102", "배송을 찾을 수 없습니다."),

	// 배송 담당자
	HUB_DELIVERY_STAFF_NOT_AVAILABLE(HttpStatus.NOT_FOUND, "DR201", "배정 가능한 허브 배송담당자가 없습니다."),
	COMPANY_DELIVERY_STAFF_NOT_AVAILABLE(HttpStatus.NOT_FOUND, "DR202", "배정 가능한 업체 배송담당자가 없습니다."),

	// 외부 서비스
	HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "DR401", "허브를 찾을 수 없습니다."),
	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR402", "업체를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR403", "사용자를 찾을 수 없습니다."),
	EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DR404", "외부 서비스 호출에 실패했습니다."),
	HUB_ROUTE_INVALID(HttpStatus.UNPROCESSABLE_ENTITY, "DR405", "허브 경로 정보가 유효하지 않습니다."),

	// 락
	DELIVERY_STAFF_ASSIGN_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "DR301", "배송 담당자 배정 락 획득에 실패했습니다.")

	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
