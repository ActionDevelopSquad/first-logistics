package com.firstlogistics.deliverservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliverySuccessCode implements SuccessCode {

	DELIVERY_CREATED(HttpStatus.CREATED, "DR_S001", "배송이 생성되었습니다."),
	DELIVERY_LIST_FOUND(HttpStatus.OK, "DR_S002", "배송 목록 조회에 성공하였습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
