package com.firstlogistics.companyservice.domain.event;

import java.util.UUID;

public record CompanyCreatedEvent(
        UUID companyId,
        String companyName
) {
}
