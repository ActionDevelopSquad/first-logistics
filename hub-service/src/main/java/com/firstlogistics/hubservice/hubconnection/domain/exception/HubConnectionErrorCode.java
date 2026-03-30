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
    ALREADY_HUB_CONNECTION_INACTIVE(HttpStatus.CONFLICT, "HBC_E09", "이미 비활성화된 허브 연결 정보입니다."),
    INVALID_HUB_CONNECTION_STATUS(HttpStatus.BAD_REQUEST, "HBC_E010", "허브 연결 정보 상태가 유효하지 않습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "HBC_E011", "허브 ID가 유효하지 않습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
