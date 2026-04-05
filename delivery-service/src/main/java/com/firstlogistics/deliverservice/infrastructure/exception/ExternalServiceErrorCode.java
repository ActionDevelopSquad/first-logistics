package com.firstlogistics.deliverservice.infrastructure.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExternalServiceErrorCode implements ErrorCode {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "DR_E407", "외부 서비스 요청이 유효하지 않습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "DR_E408", "외부 서비스 인증에 실패했습니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "DR_E409", "외부 서비스 접근 권한이 없습니다."),
	CONFLICT(HttpStatus.CONFLICT, "DR_E410", "외부 서비스 리소스 충돌이 발생했습니다."),
	PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, "DR_E411", "외부 서비스 사전 조건이 충족되지 않았습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DR_E404", "외부 서비스 호출에 실패했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
