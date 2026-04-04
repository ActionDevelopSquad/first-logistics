package com.firstlogistics.productservice.infrastructure.client.dto;

import java.util.UUID;

public record CompanyClientResponse(
        UUID id,
        UUID hubId,
        UUID managerId,
        String name,
        String type,
        String status
) {}
