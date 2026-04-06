package com.firstlogistics.hubservice.hubManager.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum HubManagerErrorCode implements ErrorCode {
    INVALID_HUB_MANAGER_ID(HttpStatus.BAD_REQUEST, "HBM_E001", "허브 관리자 아이디가 유효하지 않습니다."),
    HUB_MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "HBM_E002", "허브 관리자가 존재하지 않습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "HBM_E003", "유저 아이디가 유효하지 않습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "HBM_E004", "허브 아이디가 유효하지 않습니다."),
    INVALID_HUB_MANAGER_SORT(HttpStatus.BAD_REQUEST, "HBM_E005", "정렬 형식이 유효하지 않습니다."),
    DUPLICATE_HUB_MANAGER(HttpStatus.CONFLICT, "HBM_E006", "이미 존재하는 허브 매니저입니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HBM_E007", "해당 허브가 존재하지 않습니다."),
    SAME_HUB_MANAGER_HUB(HttpStatus.BAD_REQUEST,"HBM_E008",  "현재와 동일한 허브로는 변경할 수 없습니다."),
    FORBIDDEN_HUB_MANAGER_ACCESS(HttpStatus.FORBIDDEN,"HBM_E009", "해당 허브 관리자 정보에 접근할 권한이 없습니다."),
    INVALID_DELETED_BY(HttpStatus.BAD_REQUEST,"HBM_E010", "삭제자 정보가 유효하지 않습니다.");




    private final HttpStatus status;
    private final String code;
    private final String message;
}
