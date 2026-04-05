package com.firstlogistics.deliverservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerSuccessCode implements SuccessCode {

	DELIVERY_MANAGER_CREATED(HttpStatus.CREATED, "DM_S001", "배송 담당자가 등록되었습니다."),
	DELIVERY_MANAGER_LIST_FOUND(HttpStatus.OK, "DM_S002", "배송 담당자 목록을 조회했습니다."),
	DELIVERY_MANAGER_FOUND(HttpStatus.OK, "DM_S003", "배송 담당자를 조회했습니다."),
	DELIVERY_MANAGER_UPDATED(HttpStatus.OK, "DM_S004", "배송 담당자가 수정되었습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
