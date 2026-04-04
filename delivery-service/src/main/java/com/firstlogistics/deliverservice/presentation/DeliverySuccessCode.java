package com.firstlogistics.deliverservice.presentation;

import common.response.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliverySuccessCode implements SuccessCode {

	DELIVERY_CREATED(HttpStatus.CREATED, "DR_S001", "배송이 생성되었습니다."),
	DELIVERY_LIST_FOUND(HttpStatus.OK, "DR_S002", "배송 목록 조회에 성공하였습니다."),
	DELIVERY_DETAIL_FOUND(HttpStatus.OK, "DR_S003", "배송 상세 조회에 성공하였습니다."),
	DELIVERY_UPDATED(HttpStatus.OK, "DR_S004", "배송 정보가 수정되었습니다."),
	DELIVERY_STARTED(HttpStatus.OK, "DR_S005", "배송이 시작되었습니다."),
	DELIVERY_HUB_ARRIVED(HttpStatus.OK, "DR_S006", "허브에 도착하였습니다."),
	DELIVERY_COMPANY_STARTED(HttpStatus.OK, "DR_S007", "업체 배송이 시작되었습니다."),
	DELIVERY_COMPLETED(HttpStatus.OK, "DR_S008", "배송이 완료되었습니다."),
	DELIVERY_RECEIVED(HttpStatus.OK, "DR_S009", "입고가 완료되었습니다."),
	DELIVERY_CANCELLED(HttpStatus.OK, "DR_S010", "배송이 취소되었습니다."),
	DELIVERY_DELETED(HttpStatus.OK, "DR_S011", "배송이 삭제되었습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
