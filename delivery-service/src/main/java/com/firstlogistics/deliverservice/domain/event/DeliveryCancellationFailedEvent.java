package com.firstlogistics.deliverservice.domain.event;

import java.util.UUID;

/**
 * 배송 취소 실패 이벤트 (delivery.cancellation.failed 토픽)
 * order-service가 소비하여 주문 취소 보상 처리
 */
public record DeliveryCancellationFailedEvent(
	UUID orderId,
	String reason
) {
	public static DeliveryCancellationFailedEvent create(UUID orderId, String reason) {
		return new DeliveryCancellationFailedEvent(orderId, reason);
	}
}
