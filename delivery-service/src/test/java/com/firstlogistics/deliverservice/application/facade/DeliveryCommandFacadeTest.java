package com.firstlogistics.deliverservice.application.facade;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.CreateDeliveryResult;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.application.port.DistributedLockPort;
import com.firstlogistics.deliverservice.infrastructure.exception.InfraErrorCode;
import com.firstlogistics.deliverservice.infrastructure.exception.InfraException;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryCommandFacadeTest {

	private static final UUID STUB_SOURCE_HUB_ID = UUID.randomUUID();
	private static final UUID STUB_DESTINATION_HUB_ID = UUID.randomUUID();

	@Mock
	private DeliveryCommandService deliveryCommandService;

	@Mock
	private CompanyPort companyPort;

	@Mock
	private HubPort hubPort;

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
				.willThrow(new InfraException(InfraErrorCode.COMPANY_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.createDeliveryBySystem(command));

			// then
			assertThat(throwable)
				.isInstanceOf(InfraException.class)
				.hasFieldOrPropertyWithValue("errorCode", InfraErrorCode.COMPANY_NOT_FOUND);
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
				.willThrow(new InfraException(InfraErrorCode.HUB_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandFacade.createDeliveryBySystem(command));

			// then
			assertThat(throwable)
				.isInstanceOf(InfraException.class)
				.hasFieldOrPropertyWithValue("errorCode", InfraErrorCode.HUB_NOT_FOUND);
		}
	}

	// ─── createDelivery ────────────────────────────────

	@Nested
	@DisplayName("createDelivery 성공")
	class CreateDeliverySuccess {

		@Test
		@DisplayName("배송 생성 성공")
		void createDelivery_success() {
			// given
			UUID supplierCompanyId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
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
			CreateDeliveryResult result = deliveryCommandFacade.createDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}
	}

	// ─── 헬퍼 ──────────────────────────────────────────────────────────

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
