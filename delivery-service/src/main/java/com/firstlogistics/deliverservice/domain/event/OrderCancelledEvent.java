package com.firstlogistics.deliverservice.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * 주문 취소 이벤트 (order.cancelled 토픽)
 * Jackson 역직렬화용 — create() 정적 팩토리 없음
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCancelledEvent(
	UUID orderId
) {
}
