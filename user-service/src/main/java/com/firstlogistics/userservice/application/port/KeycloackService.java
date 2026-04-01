package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import common.jpa.domain.enums.UserRole;

import java.util.List;
import java.util.UUID;

public interface KeycloackService {
    UUID signup(UserCreateCommand command);

    void updateRole(UUID userId, List<UserRole> roles);
}
