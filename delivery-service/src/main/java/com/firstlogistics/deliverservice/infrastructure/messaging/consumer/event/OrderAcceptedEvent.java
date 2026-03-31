package com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * 주문 승인 이벤트 (order.accepted 토픽)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
