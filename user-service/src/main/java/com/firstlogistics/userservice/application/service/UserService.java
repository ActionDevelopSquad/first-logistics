package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.application.dto.result.TokenInfo;
import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.port.KeycloakTokenService;
import com.firstlogistics.userservice.application.port.KeycloakService;
import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import common.jpa.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakTokenService tokenService;
    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    @Transactional
    public TokenResult login(LoginCommand command) {
        User user = userRepository.findByUsernameNotDeleted(command.username());

        user.canLogin();

        TokenInfo tokenInfo = tokenService.generate(command.username(), command.password());

        // todo 후처리
        user.recordLogin();
        userRepository.update(user);

        return new TokenResult(
                tokenInfo.accessToken(),
                tokenInfo.expiresIn(),
                tokenInfo.refreshToken(),
                tokenInfo.refreshExpiresIn(),
                tokenInfo.tokenType()
        );
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        tokenService.logout(refreshToken);
    }

    @Transactional
    public UUID signup(UserCreateCommand command) {
        // todo 소속 아이디가 존재하는지 확인하는 이벤트
        UUID userId = keycloakService.signup(command);

        User user = User.create(
                userId,
                command.username(),
                command.lastName() + command.firstName(),
                command.phone(),
                command.email(),
                command.slackId(),
                command.userRole()
        );

        userRepository.save(user);

        return userId;
    }

    @Transactional
    public void updateRole(UUID userId, UserRole role) {
        User user = userRepository.findByIdNotDeleted(userId);
        user.updateRole(role);
        userRepository.update(user);

        keycloakService.updateRole(userId, List.of(role));
    }

    @Transactional
    public void updateStatus(UUID userId, Status status) {
        User user = userRepository.findByIdNotDeleted(userId);

        // todo 보상 트랜잭션
        if (status == Status.APPROVED) {
            user.approve();
            // todo 소속 아이디와 정보들 보내주는 이벤트
        } else {
            user.reject();
            // todo 소속 아이디와 정보들 보내주는 이벤트
        }

        userRepository.update(user);
    }

    @Transactional
    public void update(UserUpdateCommand command) {
        User user = userRepository.findByIdNotDeleted(command.userId());
        user.update(command.lastName() + command.firstName(), command.email(), command.phone(), command.slackId());
        userRepository.update(user);

        keycloakService.updateUser(command);
    }

    @Transactional
    public void delete(UUID userId, UUID deletedUserId) {
        userRepository.delete(userId, deletedUserId);

        keycloakService.deleteUser(userId);
    }
}
