package com.firstlogistics.hubservice.hubManager.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubManagerSuccessCode implements SuccessCode {
    HUB_MANAGER_RETRIEVED(HttpStatus.OK, "HBM_S002", "허브 관리자 조회에 성공했습니다."),
    HUB_MANAGER_LIST_RETRIEVED(HttpStatus.OK, "HBM_S003", "허브 관리자 목록 조회에 성공했습니다."),
    HUB_MANAGER_HUB_ID_RETRIEVED(HttpStatus.OK, "HBM_S002", "허브 관리자가 소속된 허브 아이디 조회에 성공했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}