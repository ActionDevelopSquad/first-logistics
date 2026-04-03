package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.application.dto.result.TokenInfo;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.dto.result.UserResult;
import com.firstlogistics.userservice.application.port.KeycloakService;
import com.firstlogistics.userservice.application.port.KeycloakTokenService;
import com.firstlogistics.userservice.application.port.OrganizationValidationService;
import com.firstlogistics.userservice.domain.dto.UsersSpec;
import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.event.DomainEvent;
import com.firstlogistics.userservice.domain.event.UserStatusChangedEvent;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.repository.UserQueryRepository;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import common.jpa.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final KeycloakTokenService tokenService;
    private final KeycloakService keycloakService;
    private final OrganizationValidationService organizationValidationService;

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final DomainEvent event;

    @Transactional
    public TokenResult login(LoginCommand command) {
        User user = userRepository.findByUsernameNotDeleted(command.username());

        user.canLogin();

        TokenInfo tokenInfo = tokenService.generate(command.username(), command.password());

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
    public TokenResult refresh(String refreshToken) {
        TokenInfo refresh = tokenService.refresh(refreshToken);

        return new TokenResult(
                refresh.accessToken(),
                refresh.expiresIn(),
                refresh.refreshToken(),
                refresh.refreshExpiresIn(),
                refresh.tokenType()
        );
    }

    @Transactional
    public UUID signup(UserCreateCommand command) {
        // 권한별 소속 아이디가 존재하는지 확인
        organizationValidationService.validateOrganizationExists(command.organizationId(), command.userRole());

        UUID userId = keycloakService.signup(command);

        try {
            User user = User.create(
                    userId,
                    command.username(),
                    command.lastName() + command.firstName(),
                    command.phone(),
                    command.email(),
                    command.slackId(),
                    command.userRole(),
                    command.organizationId()
            );

            userRepository.save(user);

            return userId;

        } catch (Exception e) {
            if (userId != null) {
                try {
                    keycloakService.deleteUser(userId);
                    log.warn("회원가입 실패로 Keycloak 사용자 삭제 보상 완료. userId={}", userId);
                } catch (Exception deleteException) {
                    log.error(
                            "회원가입 실패 후 Keycloak 사용자 삭제 보상 실패. 수동 삭제 필요. userId={}, originalError={}, deleteError={}",
                            userId,
                            e.getMessage(),
                            deleteException.getMessage(),
                            deleteException
                    );
                }
            }
            throw e;
        }
    }

    public UserResult getUser(UUID userId) {
        return UserResult.fromDomain(userRepository.findByIdNotDeleted(userId));
    }

    public UserResult getMyPage(UUID userId) {
        return UserResult.fromDomain(userRepository.findByIdNotDeleted(userId));
    }

    public Page<UserResult> getUsers(UserGetQuery query, Pageable pageable) {
        Page<UsersSpec> specPage = userQueryRepository.getUsers(query.toSpec(), pageable);

        return specPage.map(UserResult::fromSpec);
    }

    @Transactional
    public void updateRole(UUID userId, UserRole role) {
        User user = userRepository.findByIdNotDeleted(userId);
        user.updateRole(role);
        userRepository.update(user);

        keycloakService.updateRole(userId, List.of(role));
    }

    @Transactional
    public void updateStatus(UUID userId, Status status, UUID loginId) {
        User targetUser = userRepository.findByIdNotDeleted(userId);
        User loginUser = userRepository.findByIdNotDeleted(loginId);

        // HUB_MANAGER일 경우 targetUser의 소속이 본인이 속한 허브인지 확인
        if (!loginUser.canManage(targetUser)) {
            throw new UserException(UserErrorCode.FORBIDDEN);
        }

        if (status == Status.APPROVED) {
            targetUser.approve();
        } else {
            targetUser.reject();
        }

        event.publish(UserStatusChangedEvent.of(targetUser, targetUser.getOrganizationId(), targetUser.getStatus()));

        userRepository.update(targetUser);
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
