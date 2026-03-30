package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.application.dto.TokenInfo;
import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.port.KeycloackService;
import com.firstlogistics.userservice.application.port.KeycloackTokenService;
import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import common.jpa.domain.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private KeycloackTokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("login")
    class LoginTest {

        @Test
        @DisplayName("승인된 사용자만 로그인 성공")
        void login_success() {
            // given
            LoginCommand command = new LoginCommand("testUser", "Test1234@");
            TokenInfo tokenInfo = new TokenInfo(
                    "access-token",
                    1800,
                    "refresh-token",
                    3600,
                    "Bearer"
            );

            User user = approvedUser();

            given(userRepository.findByUsername(command.username())).willReturn(user);
            given(tokenService.generate(command.username(), command.password())).willReturn(tokenInfo);

            // when
            TokenResult result = userService.login(command);

            // then
            assertEquals(tokenInfo.access_token(), result.accessToken());
            assertEquals(tokenInfo.expires_in(), result.expiresIn());
            assertEquals(tokenInfo.refresh_token(), result.refreshToken());
            assertEquals(tokenInfo.refresh_expires_in(), result.refreshExpiresIn());
            assertEquals(tokenInfo.token_type(), result.tokenType());
            assertThat(user.getLastLoginAt()).isNotNull();

            then(userRepository).should(times(1)).findByUsername(command.username());
            then(tokenService).should(times(1)).generate(command.username(), command.password());
        }

        @Test
        @DisplayName("승인되지 않은 사용자는 로그인 실패")
        void login_fail_when_user_not_approved() {
            // given
            LoginCommand command = new LoginCommand("testUser", "Test1234@");
            TokenInfo tokenInfo = new TokenInfo(
                    "access-token",
                    1800,
                    "refresh-token",
                    3600,
                    "Bearer"
            );

            User user = pendingUser();

            given(userRepository.findByUsername(command.username())).willReturn(user);

            // when & then
            assertThatThrownBy(() -> userService.login(command))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.CAN_LOGIN_ONLY_APPROVE.getMessage());

            then(userRepository).should(times(1)).findByUsername(command.username());
            then(tokenService).should(never()).generate(any(), any());
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTest {

        @Test
        @DisplayName("refresh token이 존재하면 로그아웃 성공")
        void logout_success() {
            // given
            String refreshToken = "refresh-token";

            // when
            userService.logout(refreshToken);

            // then
            then(tokenService).should(times(1)).logout(refreshToken);
        }

        @Test
        @DisplayName("refresh token이 null이면 로그아웃 실패")
        void logout_fail_when_refresh_token_is_null() {
            // when & then
            assertThatThrownBy(() -> userService.logout(null))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());

            then(tokenService).should(never()).logout(any());
        }

        @Test
        @DisplayName("refresh token이 blank면 로그아웃 실패")
        void logout_fail_when_refresh_token_is_blank() {
            // when & then
            assertThatThrownBy(() -> userService.logout("   "))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());

            then(tokenService).should(never()).logout(any());
        }
    }

    private User pendingUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트유저",
                "010-1234-5678",
                "slack-123",
                Status.PENDING,
                UserRole.COMPANY_MANAGER,
                null
        );
    }

    private User approvedUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트유저",
                "010-1234-5678",
                "slack-123",
                Status.APPROVE,
                UserRole.COMPANY_MANAGER,
                null
        );
    }

    private User rejectedUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트유저",
                "010-1234-5678",
                "slack-123",
                Status.REJECTED,
                UserRole.COMPANY_MANAGER,
                null
        );
    }
}