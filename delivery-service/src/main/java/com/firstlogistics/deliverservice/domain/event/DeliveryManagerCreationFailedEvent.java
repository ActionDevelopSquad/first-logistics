package com.firstlogistics.deliverservice.domain.event;

import java.util.UUID;

/**
 * 배송담당자 생성 실패 이벤트 (delivery-manager.creation.failed 토픽)
 * user-service가 소비하여 승인 상태 롤백
 */
public record DeliveryManagerCreationFailedEvent(
	UUID userId,
	String reason
) {
	public static DeliveryManagerCreationFailedEvent create(UUID userId, String reason) {
		return new DeliveryManagerCreationFailedEvent(userId, reason);
	}
}
