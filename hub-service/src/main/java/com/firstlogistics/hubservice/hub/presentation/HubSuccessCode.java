package com.firstlogistics.hubservice.hub.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubSuccessCode implements SuccessCode {

    HUB_CREATED(HttpStatus.CREATED, "HB_S001", "허브가 생성되었습니다."),
    HUB_RETRIEVED(HttpStatus.OK, "HB_S002", "허브 조회에 성공했습니다."),
    HUB_LIST_RETRIEVED(HttpStatus.OK, "HB_S003", "허브 목록 조회에 성공했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
