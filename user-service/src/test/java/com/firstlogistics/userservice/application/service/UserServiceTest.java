package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import com.firstlogistics.userservice.application.dto.result.TokenInfo;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.port.KeycloakService;
import com.firstlogistics.userservice.application.port.KeycloakTokenService;
import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.event.DomainEvent;
import com.firstlogistics.userservice.domain.event.UserStatusChangedEvent;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import common.jpa.entity.enums.UserRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private KeycloakTokenService tokenService;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DomainEvent event;

    @InjectMocks
    private UserService userService;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("로그인")
    class LoginTest {

        @Test
        @Order(1)
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

            given(userRepository.findByUsernameNotDeleted(command.username())).willReturn(user);
            given(tokenService.generate(command.username(), command.password())).willReturn(tokenInfo);

            LocalDateTime beforeLogin = LocalDateTime.now();

            // when
            TokenResult result = userService.login(command);

            // then
            assertEquals(tokenInfo.accessToken(), result.accessToken());
            assertEquals(tokenInfo.expiresIn(), result.expiresIn());
            assertEquals(tokenInfo.refreshToken(), result.refreshToken());
            assertEquals(tokenInfo.refreshExpiresIn(), result.refreshExpiresIn());
            assertEquals(tokenInfo.tokenType(), result.tokenType());
            assertThat(user.getLastLoginAt()).isNotNull();

            assertThat(user.getLastLoginAt())
                    .isNotNull()
                    .isAfterOrEqualTo(beforeLogin);

            then(userRepository).should(times(1)).findByUsernameNotDeleted(command.username());
            then(tokenService).should(times(1)).generate(command.username(), command.password());
        }

        @Test
        @Order(2)
        @DisplayName("PENDING 사용자는 로그인 실패")
        void login_fail_when_user_pending() {
            // given
            LoginCommand command = new LoginCommand("testUser", "Test1234@");

            User pendingUser = pendingUser();

            given(userRepository.findByUsernameNotDeleted(command.username())).willReturn(pendingUser);

            // when & then
            assertThatThrownBy(() -> userService.login(command))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.CAN_LOGIN_ONLY_APPROVE.getMessage());

            then(userRepository).should(times(1)).findByUsernameNotDeleted(command.username());
            then(tokenService).should(never()).generate(any(), any());
        }

        @Test
        @Order(3)
        @DisplayName("REJECTED 사용자는 로그인 실패")
        void login_fail_when_user_rejected() {
            // given
            LoginCommand command = new LoginCommand("testUser", "Test1234@");

            User rejectedUser = rejectedUser();

            given(userRepository.findByUsernameNotDeleted(command.username())).willReturn(rejectedUser);

            // when & then
            assertThatThrownBy(() -> userService.login(command))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.CAN_LOGIN_ONLY_APPROVE.getMessage());

            then(userRepository).should(times(1)).findByUsernameNotDeleted(command.username());
            then(tokenService).should(never()).generate(any(), any());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("로그아웃")
    class LogoutTest {

        @Test
        @Order(1)
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
        @Order(2)
        @DisplayName("refresh token이 null이면 로그아웃 실패")
        void logout_fail_when_refresh_token_is_null() {
            // when & then
            assertThatThrownBy(() -> userService.logout(null))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());

            then(tokenService).should(never()).logout(any());
        }

        @Test
        @Order(3)
        @DisplayName("refresh token이 blank면 로그아웃 실패")
        void logout_fail_when_refresh_token_is_blank() {
            // when & then
            assertThatThrownBy(() -> userService.logout("   "))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());

            then(tokenService).should(never()).logout(any());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("회원가입")
    class SignupTest {

        @Test
        @Order(1)
        @DisplayName("회원가입에 성공하면 keycloak 회원 생성 후 userRepository에 저장")
        void signup_success() {
            // given
            UUID userId = UUID.randomUUID();

            UserCreateCommand command = new UserCreateCommand(
                    "testUser",
                    "Test1234@",
                    "길동",
                    "홍",
                    "010-1234-5678",
                    "test@google.com",
                    "slack-123",
                    UserRole.COMPANY_MANAGER,
                    UUID.randomUUID()
            );

            given(keycloakService.signup(command)).willReturn(userId);

            // when
            UUID result = userService.signup(command);

            // then
            assertThat(result).isEqualTo(userId);

            then(keycloakService).should(times(1)).signup(command);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            then(userRepository).should(times(1)).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getId()).isEqualTo(userId);
            assertThat(savedUser.getUsername()).isEqualTo(command.username());
            assertThat(savedUser.getName()).isEqualTo(command.lastName() + command.firstName());
            assertThat(savedUser.getPhone()).isEqualTo(command.phone());
            assertThat(savedUser.getSlackId()).isEqualTo(command.slackId());
            assertThat(savedUser.getUserRole()).isEqualTo(command.userRole());
        }

        @Test
        @Order(2)
        @DisplayName("keycloak에서 아이디 또는 이메일 중복이면 회원가입 실패")
        void signup_fail_when_duplicate_user() {
            // given
            UserCreateCommand command = new UserCreateCommand(
                    "testUser",
                    "Test1234@",
                    "길동",
                    "홍",
                    "010-1234-5678",
                    "test@google.com",
                    "slack-123",
                    UserRole.COMPANY_MANAGER,
                    UUID.randomUUID()
            );

            given(keycloakService.signup(command))
                    .willThrow(new UserException(UserErrorCode.DUPLICATED_USERNAME));

            // when & then
            assertThatThrownBy(() -> userService.signup(command))
                    .isInstanceOf(UserException.class)
                    .extracting("errorCode")
                    .isEqualTo(UserErrorCode.DUPLICATED_USERNAME);

            then(keycloakService).should(times(1)).signup(command);
            then(userRepository).should(never()).save(any());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("권한 변경")
    class UpdateRoleTest {

        @Test
        @Order(1)
        @DisplayName("권한 변경 시 keycloak role 업데이트를 호출")
        void updateRole_success() {
            // given
            UUID userId = UUID.randomUUID();
            UserRole role = UserRole.MASTER;

            User user = approvedUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when
            userService.updateRole(userId, role);

            // then
            then(userRepository).should(times(1)).findByIdNotDeleted(userId);
            then(userRepository).should(times(1)).update(user);
            then(keycloakService).should(times(1)).updateRole(userId, List.of(role));
        }

        @Test
        @Order(2)
        @DisplayName("변경 권한 == 수정 권한 시 실패")
        void updateRole_fail() {
            // given
            UUID userId = UUID.randomUUID();

            User user = approvedUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when
            assertThatThrownBy(() -> userService.updateRole(userId, user.getUserRole()))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining("현재 권한과 수정된 권한이 동일합니다.");

            // then
            then(userRepository).should(times(1)).findByIdNotDeleted(userId);
            then(userRepository).should(never()).update(any());
            then(keycloakService).should(never()).updateRole(any(), any());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("상태 변경")
    class UpdateStatusTest {

        @Test
        @Order(1)
        @DisplayName("승인 요청이면 approve() 호출 및 이벤트 발행")
        void updateStatus_approve() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = UUID.randomUUID();
            User user = pendingUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when
            userService.updateStatus(userId, Status.APPROVED, loginId);

            // then
            assertThat(user.getStatus()).isEqualTo(Status.APPROVED);
            then(userRepository).should(times(1)).findByIdNotDeleted(loginId);
            then(userRepository).should(times(1)).update(user);

            ArgumentCaptor<UserStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(UserStatusChangedEvent.class);
            then(event).should(times(1)).publish(eventCaptor.capture());

            UserStatusChangedEvent publishedEvent = eventCaptor.getValue();
            assertThat(publishedEvent.userId()).isEqualTo(user.getId());
            assertThat(publishedEvent.organizationId()).isEqualTo(user.getOrganizationId());
            assertThat(publishedEvent.userRole()).isEqualTo(user.getUserRole());
            assertThat(publishedEvent.status()).isEqualTo(Status.APPROVED);
            assertThat(publishedEvent.previousStatus()).isEqualTo(Status.PENDING);
        }

        @Test
        @Order(2)
        @DisplayName("이미 승인된 상태면 오류이며 이벤트 미발행")
        void updateStatus_approve_fail() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = UUID.randomUUID();
            User user = approvedUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> userService.updateStatus(userId, Status.APPROVED, loginId))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.ALREADY_APPROVE.getMessage());

            then(userRepository).should(times(1)).findByIdNotDeleted(any());
            then(userRepository).should(never()).update(any());
            then(event).should(never()).publish(any());
        }

        @Test
        @Order(3)
        @DisplayName("거절 요청이면 reject() 호출 및 이벤트 발행")
        void updateStatus_rejected() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = UUID.randomUUID();
            User user = pendingUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when
            userService.updateStatus(userId, Status.REJECTED, loginId);

            // then
            assertThat(user.getStatus()).isEqualTo(Status.REJECTED);
            then(userRepository).should().findByIdNotDeleted(userId);
            then(userRepository).should(times(1)).update(user);

            ArgumentCaptor<UserStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(UserStatusChangedEvent.class);
            then(event).should(times(1)).publish(eventCaptor.capture());

            UserStatusChangedEvent publishedEvent = eventCaptor.getValue();
            assertThat(publishedEvent.userId()).isEqualTo(user.getId());
            assertThat(publishedEvent.organizationId()).isEqualTo(user.getOrganizationId());
            assertThat(publishedEvent.userRole()).isEqualTo(user.getUserRole());
            assertThat(publishedEvent.status()).isEqualTo(Status.REJECTED);
            assertThat(publishedEvent.previousStatus()).isEqualTo(Status.PENDING);
        }

        @Test
        @Order(4)
        @DisplayName("이미 거절된 상태면 오류이며 이벤트 미발행")
        void updateStatus_rejected_fail() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = UUID.randomUUID();
            User user = rejectedUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> userService.updateStatus(userId, Status.REJECTED, loginId))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.ALREADY_REJECTED.getMessage());

            then(userRepository).should(times(1)).findByIdNotDeleted(any());
            then(userRepository).should(never()).update(user);
            then(event).should(never()).publish(any());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("회원 수정")
    class UpdateTest {

        @Test
        @Order(1)
        @DisplayName("회원 수정 성공")
        void update_success() {
            // given
            UUID userId = UUID.randomUUID();

            UserUpdateCommand command = new UserUpdateCommand(
                    userId,
                    "길동",
                    "홍",
                    "testupdate@google.com",
                    "010-1234-1234",
                    "slack-update"
            );

            User user = approvedUser();
            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            // when
            userService.update(command);

            // then
            then(userRepository).should(times(1)).findByIdNotDeleted(userId);
            then(userRepository).should(times(1)).update(user);
            then(keycloakService).should(times(1)).updateUser(command);

            assertEquals("홍길동", user.getName());
            assertEquals("testupdate@google.com", user.getEmail());
            assertEquals("010-1234-1234", user.getPhone());
            assertEquals("slack-update", user.getSlackId());
        }

        @Test
        @Order(2)
        @DisplayName("존재하지 않는 회원이면 수정 실패")
        void update_fail_when_user_not_found() {
            // given
            UUID userId = UUID.randomUUID();

            UserUpdateCommand command = new UserUpdateCommand(
                    userId,
                    "길동",
                    "홍",
                    "testupdate@google.com",
                    "010-1234-1234",
                    "slack-update"
            );

            given(userRepository.findByIdNotDeleted(userId))
                    .willThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> userService.update(command))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getMessage());

            then(userRepository).should(times(1)).findByIdNotDeleted(userId);
            then(userRepository).should(never()).update(any());
            then(keycloakService).should(never()).updateUser(any());
        }

        @Test
        @Order(3)
        @DisplayName("keycloak 회원 수정 중 예외가 발생하면 예외 전파")
        void update_fail_when_keycloak_update_throws_exception() {
            // given
            UUID userId = UUID.randomUUID();

            UserUpdateCommand command = new UserUpdateCommand(
                    userId,
                    "길동",
                    "홍",
                    "testupdate@google.com",
                    "010-1234-1234",
                    "slack-update"
            );

            User user = approvedUser();

            given(userRepository.findByIdNotDeleted(userId)).willReturn(user);

            willThrow(new UserException(UserErrorCode.AUTH_SERVER_INTERNAL_ERROR))
                    .given(keycloakService).updateUser(command);

            // when & then
            assertThatThrownBy(() -> userService.update(command))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.AUTH_SERVER_INTERNAL_ERROR.getMessage());

            then(userRepository).should(times(1)).findByIdNotDeleted(userId);
            then(userRepository).should(times(1)).update(user);
            then(keycloakService).should(times(1)).updateUser(command);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("회원 탈퇴")
    class DeleteUserTest {

        @Test
        @Order(1)
        @DisplayName("회원 탈퇴 성공")
        void delete_success() {
            // given
            UUID userId = UUID.randomUUID();
            UUID deletedUserId = UUID.randomUUID();

            willDoNothing().given(userRepository).delete(userId, deletedUserId);

            // when
            userService.delete(userId, deletedUserId);

            // then
            then(userRepository).should(times(1)).delete(userId, deletedUserId);
            then(keycloakService).should(times(1)).deleteUser(userId);

            InOrder inOrder = inOrder(userRepository, keycloakService);
            inOrder.verify(userRepository).delete(userId, deletedUserId);
            inOrder.verify(keycloakService).deleteUser(userId);
        }

        @Test
        @Order(2)
        @DisplayName("DB 삭제 중 예외가 발생하면 Keycloak 삭제는 호출 X")
        void delete_fail_when_repository_throws_exception() {
            // given
            UUID userId = UUID.randomUUID();
            UUID deletedUserId = UUID.randomUUID();

            willThrow(new RuntimeException("DB 삭제 실패"))
                    .given(userRepository).delete(userId, deletedUserId);

            // when & then
            assertThatThrownBy(() -> userService.delete(userId, deletedUserId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DB 삭제 실패");

            then(userRepository).should(times(1)).delete(userId, deletedUserId);
            then(keycloakService).should(never()).deleteUser(any(UUID.class));
        }

        @Test
        @Order(3)
        @DisplayName("Keycloak 삭제 중 예외가 발생하면 예외가 전파")
        void delete_fail_when_keycloak_throws_exception() {
            // given
            UUID userId = UUID.randomUUID();
            UUID deletedUserId = UUID.randomUUID();

            willDoNothing().given(userRepository).delete(userId, deletedUserId);
            willThrow(new RuntimeException("Keycloak 삭제 실패"))
                    .given(keycloakService).deleteUser(userId);

            // when & then
            assertThatThrownBy(() -> userService.delete(userId, deletedUserId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Keycloak 삭제 실패");

            then(userRepository).should(times(1)).delete(userId, deletedUserId);
            then(keycloakService).should(times(1)).deleteUser(userId);
        }
    }

    private User pendingUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트유저",
                "010-1234-5678",
                "test@google.com",
                "slack-123",
                Status.PENDING,
                UserRole.COMPANY_MANAGER,
                UUID.randomUUID(),
                null
        );
    }

    private User approvedUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트유저",
                "010-1234-5678",
                "test@google.com",
                "slack-123",
                Status.APPROVED,
                UserRole.COMPANY_MANAGER,
                UUID.randomUUID(),
                null
        );
    }

    private User rejectedUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트유저",
                "010-1234-5678",
                "test@google.com",
                "slack-123",
                Status.REJECTED,
                UserRole.COMPANY_MANAGER,
                UUID.randomUUID(),
                null
        );
    }
}