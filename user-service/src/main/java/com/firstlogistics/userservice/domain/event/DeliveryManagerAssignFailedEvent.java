package com.firstlogistics.userservice.domain.event;

import java.util.UUID;

public record DeliveryManagerAssignFailedEvent(
    UUID userId,
    UUID organizationId
) implements UserEvents
{}