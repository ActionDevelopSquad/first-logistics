package com.firstlogistics.userservice.application.dto.command;

import common.jpa.domain.enums.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserCreateCommand(
        String username,
        String password,
        String firstName,
        String lastName,
        String phone,
        String slackId,
        UserRole userRole,
        UUID organizationId
) {}