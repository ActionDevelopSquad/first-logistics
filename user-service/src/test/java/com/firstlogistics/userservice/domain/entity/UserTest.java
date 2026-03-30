package com.firstlogistics.userservice.domain.entity;

import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import common.jpa.domain.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("User 도메인 단위 테스트")
class UserTest {

    @Nested
    @DisplayName("canLogin")
    class CanLoginTest {

        @DisplayName("승인된 사용자는 로그인 가능")
        @Test
        void canLogin_success_whenApproved() {
            // given
            User user = reconstituteUser(Status.APPROVE);

            // when & then
            assertDoesNotThrow(user::canLogin);
        }

        @DisplayName("승인되지 않은 사용자는 로그인 불가")
        @Test
        void canLogin_fail_whenPending() {
            // given
            User user = reconstituteUser(Status.PENDING);

            // when & then
            assertThatThrownBy(user::canLogin)
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.CAN_LOGIN_ONLY_APPROVE.getMessage());
        }
    }

    @Nested
    @DisplayName("recordLogin")
    class RecordLoginTest {

        @DisplayName("로그인 시간 기록")
        @Test
        void recordLogin_success() {
            // given
            User user = reconstituteUser(Status.APPROVE);

            // when
            user.recordLogin();

            // then
            assertThat(user.getLastLoginAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("approve")
    class ApproveTest {

        @DisplayName("PENDING 상태의 회원만 승인 가능")
        @Test
        void user_approve() {
            // given
            User user = reconstituteUser(Status.PENDING);

            // when
            user.approve();

            // then
            assertEquals(Status.APPROVE, user.getStatus());
        }

        @DisplayName("이미 APPROVE 상태인 회원은 다시 승인 불가")
        @Test
        void user_approve_fail() {
            // given
            User user = reconstituteUser(Status.APPROVE);

            // when & then
            assertThatThrownBy(user::approve)
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.ALREADY_APPROVE.getMessage());
        }
    }

    @Nested
    @DisplayName("reject")
    class RejectTest {

        @DisplayName("PENDING & APPROVE 상태의 회원은 거절 가능")
        @Test
        void user_rejected() {
            // given
            User user = reconstituteUser(Status.PENDING);

            // when
            user.reject();

            // then
            assertEquals(Status.REJECTED, user.getStatus());
        }

        @DisplayName("이미 REJECTED 상태인 회원은 다시 거절 불가")
        @Test
        void user_rejected_fail() {
            // given
            User user = reconstituteUser(Status.REJECTED);

            // when & then
            assertThatThrownBy(user::reject)
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.ALREADY_REJECTED.getMessage());
        }
    }

    private User reconstituteUser(Status status) {
        return User.reconstitute(
                UUID.randomUUID(),
                "testuser",
                "테스트 유저",
                "010-1234-5678",
                "slackId",
                status,
                UserRole.COMPANY_MANAGER,
                null
        );
    }
}