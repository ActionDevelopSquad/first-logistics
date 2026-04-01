package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.HubStaffPort;
import com.firstlogistics.deliverservice.application.port.dto.HubStaffResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubStaffFeignAdapter implements HubStaffPort {

	private final HubClient hubClient;

	@Override
	public HubStaffResponse getHubStaff(UUID managerId) {
		return hubClient.getHubStaff(managerId).data();
	}
}
