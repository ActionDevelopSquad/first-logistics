package com.firstlogistics.deliverservice.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * 사용자 상태 변경 이벤트 (user.status.changed 토픽)
 * Jackson 역직렬화용 — create() 정적 팩토리 없음
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserStatusChangedEvent(
	UUID userId,
	UUID organizationId,
	String userRole,
	String status,
	String previousStatus,
	String username,
	String name,
	String email,
	String phone,
	String slackId
) {
}
