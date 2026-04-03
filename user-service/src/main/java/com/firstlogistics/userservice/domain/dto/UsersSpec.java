package com.firstlogistics.userservice.domain.dto;

import com.firstlogistics.userservice.domain.enums.Status;
import common.jpa.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersSpec(
        String username,
        String name,
        String phone,
        UserRole userRole,
        Status status,
        String slackId,
        UUID organizationId,
        LocalDateTime lastLoginAt
)
{}