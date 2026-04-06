package com.firstlogistics.orderservice.application.port;

import com.firstlogistics.orderservice.application.port.dto.HubManagerResponse;

import java.util.Optional;
import java.util.UUID;

public interface HubPort {
    Optional<HubManagerResponse> getHubManagerByUserId(UUID userId);
}
