package com.firstlogistics.hubservice.hubconnection.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum HubConnectionErrorCode implements ErrorCode {
    INVALID_MINUTES(HttpStatus.BAD_REQUEST, "HBC_E001", "시간 정보가 유효하지 않습니다."),
    INVALID_MINUTES_RANGE(HttpStatus.BAD_REQUEST, "HBC_E002", "시간 정보 범위가 유효하지 않습니다."),
    INVALID_METERS(HttpStatus.BAD_REQUEST, "HBC_E003", "거리 정보가 유효하지 않습니다."),
    INVALID_METERS_RANGE(HttpStatus.BAD_REQUEST, "HBC_E004", "거리 정보 범위가 유효하지 않습니다."),
    DUPLICATE_HUB_CONNECTION(HttpStatus.BAD_REQUEST, "HBC_E005", "이미 존재하는 허브 연결 정보입니다."),
    HUB_CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "HBC_E006", "해당 허브 연결 정보를 찾을 수 없습니다."),
    INVALID_HUB_CONNECTION_ID(HttpStatus.BAD_REQUEST, "HBC_E007", "허브 연결 정보 ID가 유효하지 않습니다."),
    ALREADY_HUB_CONNECTION_ACTIVE(HttpStatus.CONFLICT, "HBC_E008", "이미 활성화된 허브 연결 정보입니다."),
    ALREADY_HUB_CONNECTION_INACTIVE(HttpStatus.CONFLICT, "HBC_E009", "이미 비활성화된 허브 연결 정보입니다."),
    INVALID_HUB_CONNECTION_STATUS(HttpStatus.BAD_REQUEST, "HBC_E010", "허브 연결 정보 상태가 유효하지 않습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "HBC_E011", "허브 ID가 유효하지 않습니다."),
    SAME_SOURCE_AND_DESTINATION_HUB(HttpStatus.BAD_REQUEST, "HBC_E012", "출발 허브와 도착 허브가 같습니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HBC_E013", "해당 허브 정보를 찾을 수 없습니다."),
    INVALID_HUB_ROUTE_POLICY(HttpStatus.BAD_REQUEST, "HBC_E014", "허브 연결 라우트 정책이 유효하지 않습니다."),
    EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "HBC_E015", "서버 내부에서 에러가 발생했습니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "HBC_E016", "해당 업체 정보를 찾을 수 없습니다."),
    HUB_ROUTE_PENDING(HttpStatus.INTERNAL_SERVER_ERROR, "HBC_E017", "배송 경로를 확정할 수 없어 배송을 보류 상태로 전환합니다."),
    INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, "HBC_E008", "업체 ID가 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
