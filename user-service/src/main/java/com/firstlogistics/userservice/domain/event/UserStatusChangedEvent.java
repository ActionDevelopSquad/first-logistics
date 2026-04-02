package com.firstlogistics.userservice.domain.event;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import common.jpa.entity.enums.UserRole;

import java.util.UUID;

public record UserStatusChangedEvent(
        UUID userId,
        UUID organizationId,
        UserRole userRole,
        Status status,
        Status previousStatus,
        String username,
        String name,
        String email,
        String phone,
        String slackId
) implements UserEvents
{
    public static UserStatusChangedEvent of(User user, UUID organizationId, Status previousStatus) {
        return new UserStatusChangedEvent(
                user.getId(),
                organizationId,
                user.getUserRole(),
                user.getStatus(),
                previousStatus,
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getSlackId()
        );
    }
}