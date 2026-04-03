package com.firstlogistics.userservice.application.dto.query;

import common.jpa.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserGetQuery(
        String username,
        String name,
        String phone,
        UserRole userRole,
        String slackId,
        UUID organizationId,
        LocalDateTime lastLoginAt,
        int page,
        int size
)
{}
