package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubManagerFeignAdapter implements HubManagerPort {

	private final HubClient hubClient;

	@Override
	public HubManagerResponse getHubManager(UUID userId) {
		return hubClient.getHubManagerByUserId(userId).data();
	}
}
