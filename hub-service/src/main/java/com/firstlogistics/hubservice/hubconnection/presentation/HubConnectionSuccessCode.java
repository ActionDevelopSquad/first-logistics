package com.firstlogistics.hubservice.hubconnection.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubConnectionSuccessCode implements SuccessCode {

    HUB_CREATED(HttpStatus.CREATED, "HBC_S001", "허브 연결 정보가 생성되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
