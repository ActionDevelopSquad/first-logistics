package com.firstlogistics.deliverservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryErrorCode implements ErrorCode {

	// VO 유효성
	INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "DR_E001", "주소가 유효하지 않습니다."),
	INVALID_GEO_LOCATION(HttpStatus.BAD_REQUEST, "DR_E002", "위치 정보가 유효하지 않습니다."),
	INVALID_DISTANCE(HttpStatus.BAD_REQUEST, "DR_E003", "거리 정보는 0 이상이어야 합니다."),
	INVALID_TIME(HttpStatus.BAD_REQUEST, "DR_E004", "시간 정보는 0 이상이어야 합니다."),
	INVALID_MANAGER_DETAIL(HttpStatus.BAD_REQUEST, "DR_E005", "담당자 정보가 유효하지 않습니다."),
	INVALID_ID(HttpStatus.BAD_REQUEST, "DR_E013", "식별자가 유효하지 않습니다."),

	// Command / Query 유효성
	INVALID_COMMAND_PARAMS(HttpStatus.BAD_REQUEST, "DR_E010", "커맨드 파라미터가 유효하지 않습니다."),
	INVALID_QUERY_PARAMS(HttpStatus.BAD_REQUEST, "DR_E011", "쿼리 파라미터가 유효하지 않습니다."),
	INVALID_SEARCH_SPEC(HttpStatus.BAD_REQUEST, "DR_E012", "검색 조건이 유효하지 않습니다."),

	// 엔티티 유효성
	INVALID_DELIVERY_PARAMS(HttpStatus.BAD_REQUEST, "DR_E006", "배송 생성 파라미터가 유효하지 않습니다."),
	INVALID_DELIVERY_ROUTE_PARAMS(HttpStatus.BAD_REQUEST, "DR_E007", "배송 경로 생성 파라미터가 유효하지 않습니다."),
	INVALID_DELIVERY_MANAGER_PARAMS(HttpStatus.BAD_REQUEST, "DR_E008", "배송 담당자 생성 파라미터가 유효하지 않습니다."),
	INVALID_TIMETABLE_PARAMS(HttpStatus.BAD_REQUEST, "DR_E009", "타임테이블 생성 파라미터가 유효하지 않습니다."),

	// 배송
	DELIVERY_ALREADY_EXISTS(HttpStatus.CONFLICT, "DR_E101", "이미 배송이 존재합니다."),
	DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E102", "배송을 찾을 수 없습니다."),
	INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "DR_E103", "종료일이 시작일보다 앞설 수 없습니다."),
	INVALID_ROLE_SCOPE(HttpStatus.BAD_REQUEST, "DR_E104", "역할에 필요한 스코프 정보가 없습니다."),
	DELIVERY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DR_E105", "해당 배송에 대한 접근 권한이 없습니다."),

	// 배송 담당자
	HUB_DELIVERY_MANAGER_NOT_AVAILABLE(HttpStatus.NOT_FOUND, "DR_E201", "배정 가능한 허브 배송담당자가 없습니다."),
	COMPANY_DELIVERY_MANAGER_NOT_AVAILABLE(HttpStatus.NOT_FOUND, "DR_E202", "배정 가능한 업체 배송담당자가 없습니다."),

	// 외부 서비스
	HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E401", "허브를 찾을 수 없습니다."),
	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E402", "업체를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E403", "사용자를 찾을 수 없습니다."),
	EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DR_E404", "외부 서비스 호출에 실패했습니다."),
	HUB_ROUTE_INVALID(HttpStatus.UNPROCESSABLE_ENTITY, "DR_E405", "허브 경로 정보가 유효하지 않습니다."),

	// 락
	DELIVERY_MANAGER_ASSIGN_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "DR_E301", "배송 담당자 배정 락 획득에 실패했습니다.")

	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
