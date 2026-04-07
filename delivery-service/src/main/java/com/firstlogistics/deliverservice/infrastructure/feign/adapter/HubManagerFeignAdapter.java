package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubManagerFeignAdapter implements HubManagerPort {

	private final HubClient hubClient;

	@Override
	public HubManagerResponse getHubManager(UUID userId) {
		log.info("[Feign] hub-service 허브 관리자 조회 - userId: {}", userId);
		return hubClient.getHubManagerByUserId(userId).data();
	}
}
