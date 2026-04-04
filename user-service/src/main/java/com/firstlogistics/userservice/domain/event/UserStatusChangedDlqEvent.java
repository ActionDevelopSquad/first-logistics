package com.firstlogistics.userservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserStatusChangedDlqEvent(
        UUID userId,
        UUID organizationId,
        String userRole,
        String username,
        String previousStatus,
        String currentStatus,
        String originalTopic,
        String errorMessage,
        LocalDateTime failedAt
) implements UserEvents {
    public static UserStatusChangedDlqEvent from(
            UserStatusChangedEvent event,
            String originalTopic,
            Throwable ex
    ) {
        return new UserStatusChangedDlqEvent(
                event.userId(),
                event.hubId(),
                event.userRole().name(),
                event.username(),
                event.previousStatus().name(),
                event.status().name(),
                originalTopic,
                ex == null ? "unknown" : ex.getMessage(),
                LocalDateTime.now()
        );
    }
}