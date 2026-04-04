package com.firstlogistics.deliverservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerSuccessCode implements SuccessCode {

	DELIVERY_MANAGER_CREATED(HttpStatus.CREATED, "DM_S001", "배송 담당자가 등록되었습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
