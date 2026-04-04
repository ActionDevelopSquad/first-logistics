package com.firstlogistics.orderservice.infrastructure.feign.dto;

import java.util.UUID;

public record HubManagerResponse(
        UUID hubManagerId,
        UUID userId,
        UUID hubId
) {}