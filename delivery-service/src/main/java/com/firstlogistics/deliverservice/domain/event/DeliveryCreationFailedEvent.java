package com.firstlogistics.deliverservice.domain.event;

import java.util.UUID;

/**
 * 배송 생성 실패 이벤트 (delivery.creation.failed 토픽)
 * order-service가 소비하여 주문을 자동 취소(Saga 보상 트랜잭션)
 */
public record DeliveryCreationFailedEvent(
	UUID orderId,
	String reason
) {
	public static DeliveryCreationFailedEvent create(UUID orderId, String reason) {
		return new DeliveryCreationFailedEvent(orderId, reason);
	}
}
