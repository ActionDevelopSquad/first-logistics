package com.firstlogistics.orderservice.application.port.dto;

import java.util.UUID;

public record HubManagerResponse(
        UUID hubManagerId,
        UUID userId,
        UUID hubId
) {}