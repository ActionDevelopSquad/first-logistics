package com.firstlogistics.userservice.presentation.dto.request;

import common.jpa.entity.enums.UserRole;

public record UpdateRoleRequest(
        UserRole role
)
{}