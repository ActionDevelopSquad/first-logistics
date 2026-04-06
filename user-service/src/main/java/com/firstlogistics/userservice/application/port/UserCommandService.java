package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.domain.enums.Status;
import common.security.entity.enums.UserRole;

import java.util.UUID;

public interface UserCommandService {
    TokenResult login(LoginCommand command);

    void logout(String refreshToken);

    TokenResult refresh(String refreshToken);

    UUID signup(UserCreateCommand command);

    void updateRole(UUID userId, UserRole role);

    void updateStatus(UUID userId, Status status, UUID loginId);

    void update(UserUpdateCommand command);

    void delete(UUID userId, UUID deletedUserId);
}
