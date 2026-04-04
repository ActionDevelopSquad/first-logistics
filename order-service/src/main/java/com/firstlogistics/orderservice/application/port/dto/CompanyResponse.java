package com.firstlogistics.orderservice.application.port.dto;

import java.util.UUID;

public record CompanyResponse(
        UUID id,
        UUID hubId,
        UUID managerId
) {
}