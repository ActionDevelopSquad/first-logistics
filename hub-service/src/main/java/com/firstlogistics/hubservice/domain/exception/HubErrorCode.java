package com.firstlogistics.hubservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum HubErrorCode implements ErrorCode {
    INVALID_HUB_NAME(HttpStatus.BAD_REQUEST, "HB001", "허브 이름이 유효하지 않습니다."),
    DUPLICATE_HUB_NAME(HttpStatus.BAD_REQUEST, "HB002", "이미 존재하는 허브 이름입니다."),
    INVALID_HUB_ADDRESS(HttpStatus.BAD_REQUEST, "HB003", "허브 주소가 유효하지 않습니다."),
    INVALID_HUB_GEOLOCATION(HttpStatus.BAD_REQUEST, "HB004", "위치 정보가 유효하지 않습니다."),
    INVALID_HUB_GEOLOCATION_RANGE(HttpStatus.BAD_REQUEST, "HB005", "위도/경도 범위가 유효하지 않습니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HB006", "해당 허브를 찾을 수 없습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "HB007", "허브 ID가 유효하지 않습니다."),
    ALREADY_HUB_ACTIVE(HttpStatus.CONFLICT, "HB008", "이미 활성화된 허브입니다."),
    ALREADY_HUB_INACTIVE(HttpStatus.CONFLICT, "HB009", "이미 비활성화된 허브입니다."),


    INVALID_MINUTES(HttpStatus.BAD_REQUEST, "HB010", "시간 정보가 유효하지 않습니다."),
    INVALID_MINUTES_RANGE(HttpStatus.BAD_REQUEST, "HB011", "시간 정보 범위가 유효하지 않습니다."),
    INVALID_METERS(HttpStatus.BAD_REQUEST, "HB012", "거리 정보가 유효하지 않습니다."),
    INVALID_METERS_RANGE(HttpStatus.BAD_REQUEST, "HB013", "거리 정보 범위가 유효하지 않습니다."),
    DUPLICATE_HUB_CONNECTION(HttpStatus.BAD_REQUEST, "HB014", "이미 존재하는 허브 연결 정보입니다."),
    HUB_CONNECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "HB015", "해당 허브 연결 정보를 찾을 수 없습니다."),
    INVALID_HUB_CONNECTION_ID(HttpStatus.BAD_REQUEST, "HB016", "허브 연결 정보 ID가 유효하지 않습니다."),
    ALREADY_HUB_CONNECTION_ACTIVE(HttpStatus.CONFLICT, "HB017", "이미 활성화된 허브 연결 정보입니다."),
    ALREADY_HUB_CONNECTION_INACTIVE(HttpStatus.CONFLICT, "HB018", "이미 비활성화된 허브 연결 정보입니다."),;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
