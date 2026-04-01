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
        String username,
        String name,
        String email,
        String phone,
        String slackId
) {
    public static UserStatusChangedEvent of(User user, UUID organizationId) {
        return new UserStatusChangedEvent(
                user.getId(),
                organizationId,
                user.getUserRole(),
                user.getStatus(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getSlackId()
        );
    }
}