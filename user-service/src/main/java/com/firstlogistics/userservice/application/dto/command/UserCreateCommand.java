package com.firstlogistics.userservice.application.dto.command;

import com.firstlogistics.userservice.domain.enums.ManagerType;
import common.security.entity.enums.UserRole;

import java.util.UUID;

public record UserCreateCommand(
        String username,
        String password,
        String firstName,
        String lastName,
        String phone,
        String email,
        String slackId,
        UserRole userRole,
        UUID hubId,
        ManagerType managerType
) {}