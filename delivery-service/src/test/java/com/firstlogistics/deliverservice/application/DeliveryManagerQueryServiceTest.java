package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryManagerListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryManagerListResult;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.projection.DeliveryManagerSummaryProjection;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerQueryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.ManagerDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryManagerQueryServiceTest {

	@Mock
	private DeliveryManagerRepository deliveryManagerRepository;

	@Mock
	private DeliveryManagerQueryRepository deliveryManagerQueryRepository;

	@Mock
	private HubManagerPort hubManagerPort;

	@InjectMocks
	private DeliveryManagerQueryService deliveryManagerQueryService;

	// ===== 배송 담당자 목록 조회 =====

	@Nested
	@DisplayName("배송 담당자 목록 조회 성공")
	class GetDeliveryManagersSuccess {

		@Test
		@DisplayName("MASTER 전체 조회")
		void getDeliveryManagers_success_master() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryManagerListQuery query = new DeliveryManagerListQuery(
				"MASTER", userId, null, null, null, null, null, null, 10
			);

			given(deliveryManagerQueryRepository.findDeliveryManagers(any(DeliveryManagerSearchSpec.class)))
				.willReturn(List.of(stubSummaryProjection()));

			// when
			DeliveryManagerListResult result = deliveryManagerQueryService.getDeliveryManagers(query);

			// then
			assertThat(result.managers()).hasSize(1);
			assertThat(result.hasNext()).isFalse();
		}

		@Test
		@DisplayName("HUB_MANAGER 소속 허브만 조회")
		void getDeliveryManagers_success_hubManager() {
			// given
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			DeliveryManagerListQuery query = new DeliveryManagerListQuery(
				"HUB_MANAGER", userId, null, null, null, null, null, null, 10
			);

			given(hubManagerPort.getHubManager(userId))
				.willReturn(new HubManagerResponse(UUID.randomUUID(), hubId));
			given(deliveryManagerQueryRepository.findDeliveryManagers(any(DeliveryManagerSearchSpec.class)))
				.willReturn(List.of(stubSummaryProjection()));

			// when
			DeliveryManagerListResult result = deliveryManagerQueryService.getDeliveryManagers(query);

			// then
			assertThat(result.managers()).hasSize(1);
			assertThat(result.hasNext()).isFalse();
		}

		@Test
		@DisplayName("DELIVERY_MANAGER 본인만 조회")
		void getDeliveryManagers_success_deliveryManager() {
			// given
			UUID userId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			DeliveryManagerListQuery query = new DeliveryManagerListQuery(
				"DELIVERY_MANAGER", userId, null, null, null, null, null, null, 10
			);

			given(deliveryManagerRepository.findByUserId(userId))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId, userId)));
			given(deliveryManagerQueryRepository.findDeliveryManagers(any(DeliveryManagerSearchSpec.class)))
				.willReturn(List.of(stubSummaryProjection()));

			// when
			DeliveryManagerListResult result = deliveryManagerQueryService.getDeliveryManagers(query);

			// then
			assertThat(result.managers()).hasSize(1);
		}

		@Test
		@DisplayName("hasNext 판단 - size + 1개 반환 시 true")
		void getDeliveryManagers_success_hasNext() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryManagerListQuery query = new DeliveryManagerListQuery(
				"MASTER", userId, null, null, null, null, null, null, 10
			);


			List<DeliveryManagerSummaryProjection> projections = new ArrayList<>();
			for (int index = 0; index < 11; index++) {
				projections.add(stubSummaryProjection());
			}
			given(deliveryManagerQueryRepository.findDeliveryManagers(any(DeliveryManagerSearchSpec.class)))
				.willReturn(projections);

			// when
			DeliveryManagerListResult result = deliveryManagerQueryService.getDeliveryManagers(query);

			// then
			assertThat(result.managers()).hasSize(10);
			assertThat(result.hasNext()).isTrue();
		}
	}

	// ===== 배송 담당자 상세 조회 (managerId) =====

	@Nested
	@DisplayName("배송 담당자 상세 조회 실패")
	class GetDeliveryManagerFail {

		@Test
		@DisplayName("배송 담당자 미존재")
		void getDeliveryManager_fail_notFound() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerQueryService.getDeliveryManager(managerId, UserRole.MASTER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
		}

		@Test
		@DisplayName("HUB_MANAGER 타 허브 담당자 조회 시 접근 거부")
		void getDeliveryManager_fail_hubManagerAccessDenied() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID managerHubId = UUID.randomUUID();
			UUID requestUserHubId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, managerHubId)));
			given(hubManagerPort.getHubManager(userId))
				.willReturn(new HubManagerResponse(UUID.randomUUID(), requestUserHubId));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerQueryService.getDeliveryManager(managerId, UserRole.HUB_MANAGER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("DELIVERY_MANAGER 타인 조회 시 접근 거부")
		void getDeliveryManager_fail_deliveryManagerAccessDenied() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID requestUserId = UUID.randomUUID();
			UUID otherUserId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			UUID requestManagerId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId, otherUserId)));
			given(deliveryManagerRepository.findByUserId(requestUserId))
				.willReturn(Optional.of(stubDeliveryManager(requestManagerId, hubId, requestUserId)));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerQueryService.getDeliveryManager(managerId, UserRole.DELIVERY_MANAGER, requestUserId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}

	@Nested
	@DisplayName("배송 담당자 상세 조회 성공")
	class GetDeliveryManagerSuccess {

		@Test
		@DisplayName("MASTER 조회 성공")
		void getDeliveryManager_success_master() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId)));

			// when
			DeliveryManagerDetailResult result =
				deliveryManagerQueryService.getDeliveryManager(managerId, UserRole.MASTER, userId);

			// then
			assertThat(result.managerId()).isEqualTo(managerId);
			assertThat(result.hubId()).isEqualTo(hubId);
		}

		@Test
		@DisplayName("HUB_MANAGER 같은 허브 조회 성공")
		void getDeliveryManager_success_hubManager() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId)));
			given(hubManagerPort.getHubManager(userId))
				.willReturn(new HubManagerResponse(UUID.randomUUID(), hubId));

			// when
			DeliveryManagerDetailResult result =
				deliveryManagerQueryService.getDeliveryManager(managerId, UserRole.HUB_MANAGER, userId);

			// then
			assertThat(result.managerId()).isEqualTo(managerId);
		}

		@Test
		@DisplayName("DELIVERY_MANAGER 본인 조회 성공")
		void getDeliveryManager_success_deliveryManagerSelf() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID requestUserId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			DeliveryManager manager = stubDeliveryManager(managerId, hubId, requestUserId);

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(manager));
			given(deliveryManagerRepository.findByUserId(requestUserId))
				.willReturn(Optional.of(manager));

			// when
			DeliveryManagerDetailResult result =
				deliveryManagerQueryService.getDeliveryManager(managerId, UserRole.DELIVERY_MANAGER, requestUserId);

			// then
			assertThat(result.managerId()).isEqualTo(managerId);
		}
	}

	// ===== 배송 담당자 상세 조회 (userId) =====

	@Nested
	@DisplayName("배송 담당자 userId 조회 실패")
	class GetDeliveryManagerByUserIdFail {

		@Test
		@DisplayName("배송 담당자 미존재")
		void getDeliveryManagerByUserId_fail_notFound() {
			// given
			UUID targetUserId = UUID.randomUUID();
			UUID requestUserId = UUID.randomUUID();

			given(deliveryManagerRepository.findByUserId(targetUserId))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerQueryService.getDeliveryManagerByUserId(targetUserId, UserRole.MASTER, requestUserId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("배송 담당자 userId 조회 성공")
	class GetDeliveryManagerByUserIdSuccess {

		@Test
		@DisplayName("DELIVERY_MANAGER 본인 조회 성공")
		void getDeliveryManagerByUserId_success_self() {
			// given
			UUID targetUserId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();

			given(deliveryManagerRepository.findByUserId(targetUserId))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId)));

			// when
			DeliveryManagerDetailResult result =
				deliveryManagerQueryService.getDeliveryManagerByUserId(targetUserId, UserRole.DELIVERY_MANAGER, targetUserId);

			// then
			assertThat(result.managerId()).isEqualTo(managerId);
		}
	}

	// ===== 헬퍼 =====

	private DeliveryManagerSummaryProjection stubSummaryProjection() {
		return new DeliveryManagerSummaryProjection(
			UUID.randomUUID(), UUID.randomUUID(),
			"홍길동", "010-1234-5678",
			UUID.randomUUID(), "slack-id",
			ManagerType.HUB_DELIVERY, 1,
			LocalDateTime.now()
		);
	}

	private DeliveryManager stubDeliveryManager(UUID managerId, UUID hubId) {
		return stubDeliveryManager(managerId, hubId, UUID.randomUUID());
	}

	private DeliveryManager stubDeliveryManager(UUID managerId, UUID hubId, UUID userId) {
		return DeliveryManager.reconstitute(
			DeliveryManagerId.of(managerId),
			userId,
			ManagerDetail.of("홍길동", "010-1234-5678"),
			hubId,
			"slack-id",
			ManagerType.HUB_DELIVERY,
			1,
			new ArrayList<>()
		);
	}
}
