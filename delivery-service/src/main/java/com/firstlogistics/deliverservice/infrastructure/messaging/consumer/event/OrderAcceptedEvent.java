package com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event;

import java.util.UUID;

/**
 * 주문 승인 이벤트 (order.accepted 토픽)
 * TODO: 추후 common 모듈로 이동 예정
 */
public record OrderAcceptedEvent(
	UUID orderId,
	UUID sourceHubId,
	UUID receiverCompanyId,
	UUID receiverId,
	String roadAddress,
	String detailAddress,
	double latitude,
	double longitude
) {
}
