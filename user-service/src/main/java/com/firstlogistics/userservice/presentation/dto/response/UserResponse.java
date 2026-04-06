package com.firstlogistics.userservice.presentation.dto.response;

import com.firstlogistics.userservice.application.dto.result.UserResult;
import common.security.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        String username,
        String name,
        String phone,
        UserRole userRole,
        String slackId,
        UUID organizationId,
        LocalDateTime lastLoginAt
)
{
    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.username(),
                result.name(),
                result.phone(),
                result.userRole(),
                result.slackId(),
                result.organizationId(),
                result.lastLoginAt()
        );
    }
}