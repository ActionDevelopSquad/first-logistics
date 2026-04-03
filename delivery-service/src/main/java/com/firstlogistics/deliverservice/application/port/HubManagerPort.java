package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;

import java.util.UUID;

public interface HubManagerPort {

	HubManagerResponse getHubManager(UUID userId);
}
