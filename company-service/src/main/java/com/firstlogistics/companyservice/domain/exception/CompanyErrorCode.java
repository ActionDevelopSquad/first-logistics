package com.firstlogistics.companyservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CompanyErrorCode implements ErrorCode {
    INVALID_COMPANY_NAME(HttpStatus.BAD_REQUEST, "COM001", "회사 이름이 유효하지 않습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "COM002", "허브 ID가 유효하지 않습니다."),
    INVALID_MANAGER_ID(HttpStatus.BAD_REQUEST, "COM003", "담당자 ID가 유효하지 않습니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COM012", "업체를 찾을 수 없습니다."),
    INVALID_COMPANY_TYPE(HttpStatus.BAD_REQUEST, "COM004", "회사 유형이 유효하지 않습니다."),
    COMPANY_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "COM005", "회사가 이미 활성화 상태입니다."),
    COMPANY_ALREADY_INACTIVE(HttpStatus.BAD_REQUEST, "COM006", "회사가 이미 비활성화 상태입니다."),
    COMPANY_INACTIVE(HttpStatus.BAD_REQUEST, "COM007", "회사가 비활성화 상태입니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "COM008", "주소 정보가 유효하지 않습니다."),
    INVALID_GEO_LOCATION(HttpStatus.BAD_REQUEST, "COM009", "지리 정보가 유효하지 않습니다."),
    INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, "COM010", "회사 ID가 유효하지 않습니다."),
    HUB_LOCATION_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "COM011", "허브 서비스 가능 범위(대한민국)를 벗어난 위치입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
