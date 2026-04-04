package com.firstlogistics.userservice.domain.event;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.ManagerType;
import com.firstlogistics.userservice.domain.enums.Status;
import common.security.entity.enums.UserRole;

import java.util.UUID;

public record UserStatusChangedEvent(
        UUID userId,
        UUID hubId,
        ManagerType managerType,
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
    public static UserStatusChangedEvent of(User user, UUID hubId, Status previousStatus) {
        return new UserStatusChangedEvent(
                user.getId(),
                hubId,
                user.getManagerType(),
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