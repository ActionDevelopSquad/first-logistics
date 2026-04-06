package com.firstlogistics.hubservice.hub.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum HubErrorCode implements ErrorCode {
    INVALID_HUB_NAME(HttpStatus.BAD_REQUEST, "HB_E001", "허브 이름이 유효하지 않습니다."),
    DUPLICATE_HUB_NAME(HttpStatus.BAD_REQUEST, "HB_E002", "이미 존재하는 허브 이름입니다."),
    INVALID_HUB_ADDRESS(HttpStatus.BAD_REQUEST, "HB_E003", "허브 주소가 유효하지 않습니다."),
    INVALID_HUB_GEOLOCATION(HttpStatus.BAD_REQUEST, "HB_E004", "위치 정보가 유효하지 않습니다."),
    INVALID_HUB_GEOLOCATION_RANGE(HttpStatus.BAD_REQUEST, "HB_E005", "위도/경도 범위가 유효하지 않습니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HB_E006", "해당 허브를 찾을 수 없습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "HB_E007", "허브 ID가 유효하지 않습니다."),
    ALREADY_HUB_ACTIVE(HttpStatus.CONFLICT, "HB_E008", "이미 활성화된 허브입니다."),
    ALREADY_HUB_INACTIVE(HttpStatus.CONFLICT, "HB_E009", "이미 비활성화된 허브입니다."),
    INVALID_HUB_STATUS(HttpStatus.BAD_REQUEST, "HB_E010", "허브 상태가 유효하지 않습니다."),
    INVALID_SERVICE_AREA(HttpStatus.BAD_REQUEST, "HB_E011", "허브 서비스 가능 범위(대한민국)를 벗어난 위치입니다."),
    INVALID_HUB_SEARCH_COORDINATE(HttpStatus.BAD_REQUEST, "HB_E012", "위도와 경도는 함께 입력되어야 합니다."),
    INVALID_HUB_TYPE(HttpStatus.BAD_REQUEST, "HB_E013", "허브 타입이 유효하지 않습니다."),
    INVALID_HUB_UPDATE_REQUEST(HttpStatus.BAD_REQUEST, "HB_E014", "수정할 값이 하나 이상 필요합니다."),
    HUB_CONFLICT(HttpStatus.CONFLICT, "HB_E015", "허브 정보가 다른 요청에 의해 이미 변경되었습니다. 다시 시도해주세요.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
