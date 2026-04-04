package com.firstlogistics.userservice.presentation.dto.request;

import common.security.entity.enums.UserRole;

public record UpdateRoleRequest(
        UserRole role
)
{}