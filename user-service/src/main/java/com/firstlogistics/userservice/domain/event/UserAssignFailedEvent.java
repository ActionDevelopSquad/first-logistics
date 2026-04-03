package com.firstlogistics.userservice.domain.event;

import java.util.UUID;

public record UserAssignFailedEvent(
        UUID userId,
        UUID organizationId
) implements UserEvents
{}