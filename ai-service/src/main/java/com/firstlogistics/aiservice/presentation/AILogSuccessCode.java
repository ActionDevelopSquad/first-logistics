package com.firstlogistics.aiservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AILogSuccessCode implements SuccessCode {

    AILOG_CREATED(HttpStatus.CREATED, "AI_S001", "AI 로그 생성되었습니다."),
    AILOG_FOUND(HttpStatus.OK, "AI_S002", "AI 로그 조회되었습니다."),
    AILOG_LIST_FOUND(HttpStatus.OK, "AI_S003", "AI 로그 목록 조회되었습니다."),
    AILOG_DELETED(HttpStatus.OK, "AI_S004", "AI 로그 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
