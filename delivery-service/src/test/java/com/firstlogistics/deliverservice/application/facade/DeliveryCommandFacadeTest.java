package com.firstlogistics.deliverservice.application.facade;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.ChangeDeliveryStatusCommand;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.ChangeDeliveryStatusResult;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
import com.firstlogistics.deliverservice.application.permission.DeliveryAccessContext;
import com.firstlogistics.deliverservice.application.permission.DeliveryPermissionValidator;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.application.port.DistributedLockPort;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryCommandFacadeTest {

	private static final UUID STUB_SOURCE_HUB_ID = UUID.randomUUID();
	private static final UUID STUB_DESTINATION_HUB_ID = UUID.randomUUID();
	private static final DeliveryManagerId STUB_ROUTE_MANAGER_ID = DeliveryManagerId.generate();

	@Mock
	private DeliveryCommandService deliveryCommandService;

	@Mock
	private CompanyPort companyPort;

	@Mock
	private HubPort hubPort;

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private DeliveryPermissionValidator deliveryPermissionValidator;

	@Mock
	private DistributedLockPort distributedLockPort;

	@InjectMocks
	private DeliveryCommandFacade deliveryCommandFacade;

	// ─── createDeliveryBySystem ─────────────────────────────────────────

	@Nested
	@DisplayName("createDeliveryBySystem 실패")
	class CreateDeliveryBySystemFail {

		@Test
		@DisplayName("존재하지 않는 수령업체 ID")
		void createDeliveryBySystem_fail_receiverCompanyNotFound() {
			// given
			UUID supplierCompanyId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(supplierCompanyId, receiverCompanyId);

			given(companyPort.getCompany(supplierCompanyId))
				.willReturn(new CompanyResponse(supplierCompanyId, STUB_SOURCE_HUB_ID, "공급업체", "서울시 송파구 올림픽로 300", "A동 1층"));
			given(companyPort.getCompany(receiverCompanyId))
				.willThrow(new DeliveryException(DeliveryErrorCode.COMPANY_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.createDeliveryBySystem(command));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.COMPANY_NOT_FOUND);
		}

		@Test
		@DisplayName("존재하지 않는 허브 (경로 계산 실패)")
		void createDeliveryBySystem_fail_hubNotFound() {
			// given
			UUID supplierCompanyId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(supplierCompanyId, receiverCompanyId);

			given(companyPort.getCompany(supplierCompanyId))
				.willReturn(new CompanyResponse(supplierCompanyId, STUB_SOURCE_HUB_ID, "공급업체", "서울시 송파구 올림픽로 300", "A동 1층"));
			given(companyPort.getCompany(receiverCompanyId))
				.willReturn(new CompanyResponse(receiverCompanyId, STUB_DESTINATION_HUB_ID, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호"));
			given(hubPort.getHubRoute(STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID, receiverCompanyId))
				.willThrow(new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.createDeliveryBySystem(command));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.HUB_NOT_FOUND);
		}
	}

	// ─── createDelivery (권한 검증 포함) ────────────────────────────────

	@Nested
	@DisplayName("createDelivery 실패")
	class CreateDeliveryFail {

		@Test
		@DisplayName("MASTER 외 권한으로 배송 생성 시도")
		void createDelivery_fail_accessDenied() {
			// given
			UUID supplierCompanyId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			String role = "HUB_MANAGER";
			CreateDeliveryCommand command = stubCommand(supplierCompanyId, receiverCompanyId);

			doThrow(new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED))
				.when(deliveryPermissionValidator).validateRole(role, Set.of(UserRole.MASTER));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.createDelivery(command, role, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
			then(companyPort).should(never()).getCompany(any());
		}
	}

	@Nested
	@DisplayName("createDelivery 성공")
	class CreateDeliverySuccess {

		@Test
		@DisplayName("MASTER 권한으로 배송 생성 성공")
		void createDelivery_success() {
			// given
			UUID supplierCompanyId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			String role = "MASTER";
			CreateDeliveryCommand command = stubCommand(supplierCompanyId, receiverCompanyId);
			UUID deliveryId = UUID.randomUUID();
			CreateDeliveryResult expectedResult = new CreateDeliveryResult(deliveryId);

			given(companyPort.getCompany(supplierCompanyId))
				.willReturn(new CompanyResponse(supplierCompanyId, STUB_SOURCE_HUB_ID, "공급업체", "서울시 송파구 올림픽로 300", "A동 1층"));
			given(companyPort.getCompany(receiverCompanyId))
				.willReturn(new CompanyResponse(receiverCompanyId, STUB_DESTINATION_HUB_ID, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호"));
			given(hubPort.getHubRoute(STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID, receiverCompanyId))
				.willReturn(new HubRouteResponse(STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID, List.of(
					new HubRouteStepResponse(0, STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID, 10000, 30)
				)));
			given(distributedLockPort.executeWithMultiLock(any(), any()))
				.willReturn(expectedResult);

			// when
			CreateDeliveryResult result = deliveryCommandFacade.createDelivery(command, role, userId);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}
	}

	// ─── cancelDelivery (권한 검증 포함) ────────────────────────────────

	@Nested
	@DisplayName("cancelDelivery 실패")
	class CancelDeliveryFail {

		@Test
		@DisplayName("배송 미존재")
		void cancelDelivery_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			String role = "MASTER";
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.cancelDelivery(command, role, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}

		@Test
		@DisplayName("허용되지 않은 권한으로 취소 시도")
		void cancelDelivery_fail_accessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			String role = "COMPANY_MANAGER";
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId);
			Delivery delivery = stubDelivery(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId)))
				.willReturn(Optional.of(delivery));
			doThrow(new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED))
				.when(deliveryPermissionValidator).validate(
					any(DeliveryAccessContext.class), eq(role), eq(userId),
					eq(Set.of(UserRole.MASTER, UserRole.HUB_MANAGER))
				);

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.cancelDelivery(command, role, userId));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
			then(deliveryCommandService).should(never()).cancelDelivery(any());
		}
	}

	@Nested
	@DisplayName("cancelDelivery 성공")
	class CancelDeliverySuccess {

		@Test
		@DisplayName("MASTER 권한으로 배송 취소 성공")
		void cancelDelivery_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			String role = "MASTER";
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId);
			Delivery delivery = stubDelivery(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId)))
				.willReturn(Optional.of(delivery));
			given(deliveryCommandService.cancelDelivery(delivery))
				.willReturn(new ChangeDeliveryStatusResult(deliveryId));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandFacade.cancelDelivery(command, role, userId);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}
	}

	// ─── cancelDeliveryBySystem ─────────────────────────────────────────

	@Nested
	@DisplayName("cancelDeliveryBySystem 실패")
	class CancelDeliveryBySystemFail {

		@Test
		@DisplayName("배송 미존재")
		void cancelDeliveryBySystem_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();

			given(deliveryRepository.findById(DeliveryId.of(deliveryId)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.cancelDeliveryBySystem(ChangeDeliveryStatusCommand.of(deliveryId)));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("cancelDeliveryBySystem 성공")
	class CancelDeliveryBySystemSuccess {

		@Test
		@DisplayName("정상 취소")
		void cancelDeliveryBySystem_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDelivery(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId)))
				.willReturn(Optional.of(delivery));
			given(deliveryCommandService.cancelDelivery(delivery))
				.willReturn(new ChangeDeliveryStatusResult(deliveryId));

			// when
			deliveryCommandFacade.cancelDeliveryBySystem(ChangeDeliveryStatusCommand.of(deliveryId));

			// then
			then(deliveryCommandService).should().cancelDelivery(delivery);
		}

		@Test
		@DisplayName("이미 취소된 배송 - 멱등하게 무시")
		void cancelDeliveryBySystem_success_alreadyCancelled() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDelivery(deliveryId, DeliveryStatus.CANCELLED);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId)))
				.willReturn(Optional.of(delivery));

			// when
			deliveryCommandFacade.cancelDeliveryBySystem(ChangeDeliveryStatusCommand.of(deliveryId));

			// then
			then(deliveryCommandService).should(never()).cancelDelivery(any());
		}
	}

	// ─── 헬퍼 ──────────────────────────────────────────────────────────

	private Delivery stubDelivery(UUID deliveryId, DeliveryStatus status) {
		DeliveryId id = DeliveryId.of(deliveryId);
		DeliveryRoute route = DeliveryRoute.create(id, 0, STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID, 10000, 30);
		route.assignManager(STUB_ROUTE_MANAGER_ID);
		return Delivery.reconstitute(
			id, UUID.randomUUID(), status,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Address.of("서울시 강남구 테헤란로 123", "101호"),
			null,
			UUID.randomUUID(), "slack-receiver", UUID.randomUUID(),
			DeliveryManagerId.generate(), STUB_SOURCE_HUB_ID,
			List.of(route)
		);
	}

	private CreateDeliveryCommand stubCommand(UUID supplierCompanyId, UUID receiverCompanyId) {
		return new CreateDeliveryCommand(
			UUID.randomUUID(),
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(3),
			"요청사항",
			supplierCompanyId,
			UUID.randomUUID(),
			receiverCompanyId,
			UUID.randomUUID(),
			"서울시 강남구 테헤란로 123",
			"101호",
			List.of(new CreateDeliveryCommand.OrderItemInfo(UUID.randomUUID(), "마른 오징어", 50, 10000L))
		);
	}
}
