package com.firstlogistics.deliverservice.domain.exception;

/**
 * 배송 생성 실패 예외
 * Kafka order.accepted 컨슈머에서 발생 시 Saga 보상 트랜잭션(주문 취소) 트리거
 */
public class DeliveryCreationException extends DeliveryException {

	public DeliveryCreationException(DeliveryErrorCode errorCode) {
		super(errorCode);
	}
}
