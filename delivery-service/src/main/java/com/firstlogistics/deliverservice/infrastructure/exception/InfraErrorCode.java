package com.firstlogistics.deliverservice.infrastructure.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InfraErrorCode implements ErrorCode {

	// 외부 서비스 - HTTP 상태별
	EXTERNAL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "DR_E407", "외부 서비스 요청이 유효하지 않습니다."),
	EXTERNAL_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "DR_E408", "외부 서비스 인증에 실패했습니다."),
	EXTERNAL_FORBIDDEN(HttpStatus.FORBIDDEN, "DR_E409", "외부 서비스 접근 권한이 없습니다."),
	EXTERNAL_CONFLICT(HttpStatus.CONFLICT, "DR_E410", "외부 서비스 리소스 충돌이 발생했습니다."),
	EXTERNAL_PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, "DR_E411", "외부 서비스 사전 조건이 충족되지 않았습니다."),
	EXTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DR_E404", "외부 서비스 호출에 실패했습니다."),

	// 외부 서비스 - 404 Not Found (FeignErrorDecoder 전용)
	HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E401", "허브를 찾을 수 없습니다."),
	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E402", "업체를 찾을 수 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E403", "사용자를 찾을 수 없습니다."),
	HUB_MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E204", "허브 관리자를 찾을 수 없습니다."),
	COMPANY_MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E205", "업체 담당자를 찾을 수 없습니다."),
	HUB_CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "DR_E406", "허브 간 경로를 찾을 수 없습니다."),

	// 데이터 무결성
	DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DR_E501", "이미 존재하는 리소스입니다."),

	// 락
	LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "DR_E301", "배송 담당자 배정 락 획득에 실패했습니다."),
	OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "DR_E302", "다른 사용자가 동시에 수정하여 요청이 실패했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
