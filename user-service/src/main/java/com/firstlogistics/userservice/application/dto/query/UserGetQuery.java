package com.firstlogistics.userservice.application.dto.query;

import com.firstlogistics.userservice.domain.enums.Status;
import common.security.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserGetQuery(
        String username,
        String name,
        String phone,
        UserRole userRole,
        Status status,
        String slackId,
        UUID hubId,
        LocalDateTime lastLoginAt
)
{
    public UserGetQuery toSpec() {
        return new UserGetQuery(
                username,
                name,
                phone,
                userRole,
                status,
                slackId,
                hubId,
                lastLoginAt
        );
    }
}
