package com.firstlogistics.userservice.domain.event;

import java.util.UUID;

public record DeliveryStaffAssignFailedEvent (
    UUID userId,
    UUID organizationId
) implements UserEvents
{}