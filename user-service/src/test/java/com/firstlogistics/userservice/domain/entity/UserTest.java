package com.firstlogistics.userservice.domain.entity;

import com.firstlogistics.userservice.domain.enums.ManagerType;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import common.security.entity.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("User 도메인 단위 테스트")
class UserTest {

    @Nested
    @DisplayName("canLogin")
    class CanLoginTest {

        @DisplayName("승인된 사용자는 로그인 가능")
        @Test
        void canLogin_success_whenApproved() {
            // given
            User user = reconstituteUser(Status.APPROVED);

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
            User user = reconstituteUser(Status.APPROVED);

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
            assertEquals(Status.APPROVED, user.getStatus());
        }

        @DisplayName("이미 APPROVE 상태인 회원은 다시 승인 불가")
        @Test
        void user_approve_fail() {
            // given
            User user = reconstituteUser(Status.APPROVED);

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

    @Nested
    @DisplayName("canManage")
    class CanManageTest {

        @Test
        @DisplayName("MASTER는 모든 사용자를 관리할 수 있다")
        void masterCanManageAnyone() {
            // given
            User master = createUser(UserRole.MASTER, UUID.randomUUID(), Status.APPROVED);
            User target = createUser(UserRole.COMPANY_MANAGER, UUID.randomUUID(), Status.APPROVED);

            // when
            boolean result = master.canManage(target);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("HUB_MANAGER는 같은 허브의 COMPANY_MANAGER를 관리할 수 있다")
        void hubManagerCanManageCompanyManagerInSameHub() {
            // given
            UUID hubId = UUID.randomUUID();
            User hubManager = createUser(UserRole.HUB_MANAGER, hubId, Status.APPROVED);
            User target = createUser(UserRole.COMPANY_MANAGER, hubId, Status.APPROVED);

            // when
            boolean result = hubManager.canManage(target);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("HUB_MANAGER는 같은 허브의 DELIVERY_MANAGER를 관리할 수 있다")
        void hubManagerCanManageDeliveryManagerInSameHub() {
            // given
            UUID hubId = UUID.randomUUID();
            User hubManager = createUser(UserRole.HUB_MANAGER, hubId, Status.APPROVED);
            User target = createUser(UserRole.DELIVERY_MANAGER, hubId, Status.APPROVED);

            // when
            boolean result = hubManager.canManage(target);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("HUB_MANAGER는 허브가 다르면 관리할 수 없다")
        void hubManagerCannotManageUserInDifferentHub() {
            // given
            User hubManager = createUser(UserRole.HUB_MANAGER, UUID.randomUUID(), Status.APPROVED);
            User target = createUser(UserRole.COMPANY_MANAGER, UUID.randomUUID(), Status.APPROVED);

            // when
            boolean result = hubManager.canManage(target);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("HUB_MANAGER는 같은 허브여도 MASTER는 관리할 수 없다")
        void hubManagerCannotManageMasterEvenInSameHub() {
            // given
            UUID hubId = UUID.randomUUID();
            User hubManager = createUser(UserRole.HUB_MANAGER, hubId, Status.APPROVED);
            User target = createUser(UserRole.MASTER, hubId, Status.APPROVED);

            // when
            boolean result = hubManager.canManage(target);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("HUB_MANAGER는 같은 허브여도 HUB_MANAGER는 관리할 수 없다")
        void hubManagerCannotManageAnotherHubManagerEvenInSameHub() {
            // given
            UUID hubId = UUID.randomUUID();
            User hubManager = createUser(UserRole.HUB_MANAGER, hubId, Status.APPROVED);
            User target = createUser(UserRole.HUB_MANAGER, hubId, Status.APPROVED);

            // when
            boolean result = hubManager.canManage(target);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("COMPANY_MANAGER는 다른 사용자를 관리할 수 없다")
        void companyManagerCannotManageAnyone() {
            // given
            User companyManager = createUser(UserRole.COMPANY_MANAGER, UUID.randomUUID(), Status.APPROVED);
            User target = createUser(UserRole.DELIVERY_MANAGER, UUID.randomUUID(), Status.APPROVED);

            // when
            boolean result = companyManager.canManage(target);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("DELIVERY_MANAGER는 다른 사용자를 관리할 수 없다")
        void deliveryManagerCannotManageAnyone() {
            // given
            User deliveryManager = createUser(UserRole.DELIVERY_MANAGER, UUID.randomUUID(), Status.APPROVED);
            User target = createUser(UserRole.COMPANY_MANAGER, UUID.randomUUID(), Status.APPROVED);

            // when
            boolean result = deliveryManager.canManage(target);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("rollbackStatus")
    class RollbackStatusTest {

        @Test
        @DisplayName("상태를 PENDING으로 되돌린다")
        void rollbackStatusToPending() {
            // given
            User user = reconstituteUser(Status.APPROVED);

            // when
            user.rollbackStatus();

            // then
            assertThat(user.getStatus()).isEqualTo(Status.PENDING);
        }
    }

    @Nested
    @DisplayName("deliveryManagerOver")
    class DeliveryManagerOverTest {

        @Test
        @DisplayName("상태를 REJECTED로 변경한다")
        void changeStatusToRejected() {
            // given
            User user = reconstituteUser(Status.APPROVED);

            // when
            user.deliveryManagerOver();

            // then
            assertThat(user.getStatus()).isEqualTo(Status.REJECTED);
        }
    }

    private User reconstituteUser(Status status) {
        return User.reconstitute(
                UUID.randomUUID(),
                "testUser",
                "테스트 유저",
                "010-1234-5678",
                "test@google.com",
                "slackId",
                status,
                UserRole.COMPANY_MANAGER,
                UUID.randomUUID(),
                ManagerType.HUB_DELIVERY,
                null
        );
    }

    private User createUser(UserRole userRole, UUID hubId, Status status) {
        return User.reconstitute(
                UUID.randomUUID(),
                "username",
                "홍길동",
                "test@test.com",
                "01012345678",
                "slack-id",
                status,
                userRole,
                hubId,
                ManagerType.HUB_DELIVERY,
                null
        );
    }
}