package com.firstlogistics.hubservice.hubconnection.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubConnectionSuccessCode implements SuccessCode {

    HUB_CONNECTION_CREATED(HttpStatus.CREATED, "HBC_S001", "허브 연결 정보가 생성되었습니다."),
    HUB_ROUTES_RETRIEVED(HttpStatus.OK, "HBC_S002", "허브 경로 조회가 완료되었습니다."),
    HUB_CONNECTION_LIST_RETRIEVED(HttpStatus.OK, "HBC_S003", "허브 연결 정보 목록 조회가 완료되었습니다."),
    HUB_CONNECTION_RETRIEVED(HttpStatus.OK, "HBC_S004", "허브 연결 정보 조회가 완료되었습니다."),
    HUB_CONNECTION_UPDATED(HttpStatus.OK, "HBC_S005", "허브 연결 정보 수정이 완료되었습니다."),
    HUB_CONNECTION_DELETED(HttpStatus.OK, "HBC_S006", "허브 연결 정보 삭제가 완료되었습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
