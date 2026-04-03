package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubManagerFeignAdapter implements HubManagerPort {

	private final HubClient hubClient;

	@Override
	public HubManagerResponse getHubManager(UUID userId) {
		List<HubManagerResponse> results = hubClient.getHubManagersByUserId(userId).data();
		if (results == null || results.size() != 1) {
			throw new DeliveryException(DeliveryErrorCode.HUB_MANAGER_NOT_FOUND);
		}
		return results.get(0);
	}
}
