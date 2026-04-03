package com.firstlogistics.userservice.application.dto.result;

import com.firstlogistics.userservice.domain.entity.User;
import common.jpa.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResult(
        String username,
        String name,
        String phone,
        UserRole userRole,
        String slackId,
        UUID organizationId,
        LocalDateTime lastLoginAt
)
{
    public static UserResult from(User user) {
        return new UserResult(
                user.getUsername(),
                user.getName(),
                user.getPhone(),
                user.getUserRole(),
                user.getSlackId(),
                user.getOrganizationId(),
                user.getLastLoginAt()
        );
    }
}
