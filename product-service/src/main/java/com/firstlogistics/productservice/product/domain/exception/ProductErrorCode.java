package com.firstlogistics.productservice.product.domain.exception;

import common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProductErrorCode implements ErrorCode {
    INVALID_MONEY(HttpStatus.BAD_REQUEST, "PRO001", "금액이 유효하지 않습니다."),
    INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, "PRO002", "회사 ID가 유효하지 않습니다."),
    INVALID_PRODUCT_NAME(HttpStatus.BAD_REQUEST, "PRO003", "상품 이름이 유효하지 않습니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "PRO004", "상품 상태가 유효하지 않습니다."),
    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "PRO005", "허브 ID가 유효하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRO006", "상품을 찾을 수 없습니다."),
    PRODUCT_ALREADY_STOPPED(HttpStatus.BAD_REQUEST, "PRO007", "이미 판매 중지된 상품입니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "PRO008", "업체를 찾을 수 없습니다."),
    UNAUTHORIZED_COMPANY_ACCESS(HttpStatus.FORBIDDEN, "PRO009", "본인 업체의 상품만 등록할 수 있습니다."),
    COMPANY_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "PRO010", "업체 서비스가 일시적으로 이용 불가능합니다. 잠시 후 다시 시도해주세요."),
    UNAUTHORIZED_PRODUCT_UPDATE(HttpStatus.FORBIDDEN, "PRO011", "본인 업체의 상품만 수정할 수 있습니다."),
    PRODUCT_ALREADY_SELLING(HttpStatus.BAD_REQUEST, "PRO012", "이미 판매 중인 상품입니다."),
    UNAUTHORIZED_PRODUCT_STATUS_CHANGE(HttpStatus.FORBIDDEN, "PRO013", "본인 업체의 상품 상태만 변경할 수 있습니다."),
    INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "PRO014", "지원하지 않는 정렬 필드입니다. 허용 필드: createdAt, updatedAt"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
