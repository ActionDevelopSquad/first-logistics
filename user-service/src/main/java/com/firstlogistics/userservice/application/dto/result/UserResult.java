package com.firstlogistics.userservice.application.dto.result;

import com.firstlogistics.userservice.domain.dto.UsersSpec;
import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import common.security.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResult(
        String username,
        String name,
        String phone,
        UserRole userRole,
        Status status,
        String slackId,
        UUID organizationId,
        LocalDateTime lastLoginAt
)
{
    public static UserResult fromDomain(User user) {
        return new UserResult(
                user.getUsername(),
                user.getName(),
                user.getPhone(),
                user.getUserRole(),
                user.getStatus(),
                user.getSlackId(),
                user.getHubId(),
                user.getLastLoginAt()
        );
    }

    public static UserResult fromSpec(UsersSpec spec) {
        return new UserResult(
                spec.username(),
                spec.name(),
                spec.phone(),
                spec.userRole(),
                spec.status(),
                spec.slackId(),
                spec.organizationId(),
                spec.lastLoginAt()
        );
    }
}
