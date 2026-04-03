package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import common.jpa.entity.enums.UserRole;

import java.util.List;
import java.util.UUID;

public interface KeycloakService {
    UUID signup(UserCreateCommand command);

    void updateRole(UUID userId, List<UserRole> roles);

    void updateUser(UserUpdateCommand command);

    void deleteUser(UUID userId);
}
