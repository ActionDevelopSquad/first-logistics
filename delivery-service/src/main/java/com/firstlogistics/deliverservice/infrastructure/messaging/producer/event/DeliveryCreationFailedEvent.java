package com.firstlogistics.deliverservice.infrastructure.messaging.producer.event;

import java.util.UUID;

/**
 * 배송 생성 실패 이벤트 (delivery.creation.failed 토픽)
 * order-service가 소비하여 주문을 자동 취소(Saga 보상 트랜잭션)
 */
public record DeliveryCreationFailedEvent(
	UUID orderId,
	String reason
) {
}
