package com.firstlogistics.sampleservice.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ItemErrorCode implements ErrorCode {

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM_001", "아이템을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
