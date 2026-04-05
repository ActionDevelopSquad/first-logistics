package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryManagerCommand;
import com.firstlogistics.deliverservice.application.dto.result.UpdateDeliveryManagerResult;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.entity.ManagerTimetable;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.ManagerDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryManagerCommandServiceTest {

	@Mock
	private DeliveryManagerRepository deliveryManagerRepository;

	@Mock
	private HubManagerPort hubManagerPort;

	@Mock
	private UserPort userPort;

	@InjectMocks
	private DeliveryManagerCommandService deliveryManagerCommandService;

	// ===== 배송 담당자 수정 =====

	@Nested
	@DisplayName("배송 담당자 수정 실패")
	class UpdateDeliveryManagerFail {

		@Test
		@DisplayName("배송 담당자 미존재")
		void updateDeliveryManager_fail_notFound() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID newHubId = UUID.randomUUID();
			UpdateDeliveryManagerCommand command = new UpdateDeliveryManagerCommand(managerId, newHubId, ManagerType.HUB_DELIVERY);

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerCommandService.updateDeliveryManager(command, UserRole.MASTER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
		}

		@Test
		@DisplayName("HUB_MANAGER 타 허브 담당자 수정 시 접근 거부")
		void updateDeliveryManager_fail_hubManagerAccessDenied() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID managerHubId = UUID.randomUUID();
			UUID requestUserHubId = UUID.randomUUID();
			UUID newHubId = UUID.randomUUID();
			UpdateDeliveryManagerCommand command = new UpdateDeliveryManagerCommand(managerId, newHubId, ManagerType.COMPANY_DELIVERY);

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, managerHubId)));
			given(hubManagerPort.getHubManager(userId))
				.willReturn(new HubManagerResponse(UUID.randomUUID(), requestUserHubId));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerCommandService.updateDeliveryManager(command, UserRole.HUB_MANAGER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("활성 배송이 배정된 상태에서 수정 시도")
		void updateDeliveryManager_fail_hasActiveDelivery() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			UUID newHubId = UUID.randomUUID();
			UpdateDeliveryManagerCommand command = new UpdateDeliveryManagerCommand(managerId, newHubId, ManagerType.COMPANY_DELIVERY);

			DeliveryManager manager = stubDeliveryManager(managerId, hubId);
			manager.assignDelivery(DeliveryId.generate(), LocalDateTime.now(), LocalDateTime.now().plusHours(1));

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(manager));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerCommandService.updateDeliveryManager(command, UserRole.MASTER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_MODIFIABLE);
		}
	}

	@Nested
	@DisplayName("배송 담당자 수정 성공")
	class UpdateDeliveryManagerSuccess {

		@Test
		@DisplayName("MASTER 수정 성공")
		void updateDeliveryManager_success_master() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID currentHubId = UUID.randomUUID();
			UUID newHubId = UUID.randomUUID();
			UpdateDeliveryManagerCommand command = new UpdateDeliveryManagerCommand(managerId, newHubId, ManagerType.COMPANY_DELIVERY);

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, currentHubId)));
			given(deliveryManagerRepository.save(any(DeliveryManager.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

			// when
			UpdateDeliveryManagerResult result =
				deliveryManagerCommandService.updateDeliveryManager(command, UserRole.MASTER, userId);

			// then
			assertThat(result.managerId()).isEqualTo(managerId);
			assertThat(result.hubId()).isEqualTo(newHubId);
			assertThat(result.managerType()).isEqualTo(ManagerType.COMPANY_DELIVERY);

			ArgumentCaptor<DeliveryManager> captor = ArgumentCaptor.forClass(DeliveryManager.class);
			then(deliveryManagerRepository).should().save(captor.capture());
			assertThat(captor.getValue().getHubId()).isEqualTo(newHubId);
			assertThat(captor.getValue().getManagerType()).isEqualTo(ManagerType.COMPANY_DELIVERY);
		}

		@Test
		@DisplayName("HUB_MANAGER 같은 허브 수정 성공")
		void updateDeliveryManager_success_hubManager() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			UUID newHubId = UUID.randomUUID();
			UpdateDeliveryManagerCommand command = new UpdateDeliveryManagerCommand(managerId, newHubId, ManagerType.HUB_DELIVERY);

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId)));
			given(hubManagerPort.getHubManager(userId))
				.willReturn(new HubManagerResponse(UUID.randomUUID(), hubId));
			given(deliveryManagerRepository.save(any(DeliveryManager.class)))
				.willAnswer(invocation -> invocation.getArgument(0));

			// when
			UpdateDeliveryManagerResult result =
				deliveryManagerCommandService.updateDeliveryManager(command, UserRole.HUB_MANAGER, userId);

			// then
			assertThat(result.managerId()).isEqualTo(managerId);
			assertThat(result.hubId()).isEqualTo(newHubId);
		}
	}

	// ===== 배송 담당자 삭제 =====

	@Nested
	@DisplayName("배송 담당자 삭제 실패")
	class DeleteDeliveryManagerFail {

		@Test
		@DisplayName("배송 담당자 미존재")
		void deleteDeliveryManager_fail_notFound() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerCommandService.deleteDeliveryManager(managerId, UserRole.MASTER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
		}

		@Test
		@DisplayName("HUB_MANAGER 타 허브 담당자 삭제 시 접근 거부")
		void deleteDeliveryManager_fail_hubManagerAccessDenied() {
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
				deliveryManagerCommandService.deleteDeliveryManager(managerId, UserRole.HUB_MANAGER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("활성 배송이 배정된 상태에서 삭제 시도")
		void deleteDeliveryManager_fail_hasActiveDelivery() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();

			DeliveryManager manager = stubDeliveryManager(managerId, hubId);
			manager.assignDelivery(DeliveryId.generate(), LocalDateTime.now(), LocalDateTime.now().plusHours(1));

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(manager));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryManagerCommandService.deleteDeliveryManager(managerId, UserRole.MASTER, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_MODIFIABLE);
		}
	}

	@Nested
	@DisplayName("배송 담당자 삭제 성공")
	class DeleteDeliveryManagerSuccess {

		@Test
		@DisplayName("MASTER 삭제 성공")
		void deleteDeliveryManager_success_master() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();

			given(deliveryManagerRepository.findById(DeliveryManagerId.of(managerId)))
				.willReturn(Optional.of(stubDeliveryManager(managerId, hubId)));

			// when & then (예외 없음)
			deliveryManagerCommandService.deleteDeliveryManager(managerId, UserRole.MASTER, userId);

			then(deliveryManagerRepository).should().deleteById(DeliveryManagerId.of(managerId), userId);
		}
	}

	// ===== 헬퍼 =====

	private DeliveryManager stubDeliveryManager(UUID managerId, UUID hubId) {
		return DeliveryManager.reconstitute(
			DeliveryManagerId.of(managerId),
			UUID.randomUUID(),
			ManagerDetail.of("홍길동", "010-1234-5678"),
			hubId,
			"slack-id",
			ManagerType.HUB_DELIVERY,
			1,
			new ArrayList<>()
		);
	}
}
