package com.firstlogistics.deliverservice.application.facade;

import com.firstlogistics.deliverservice.application.DeliveryCommandService;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.redis.lock.DeliveryDistributedLockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryCreateFacadeTest {

	@Mock
	private DeliveryCommandService deliveryCommandService;

	@Mock
	private CompanyPort companyPort;

	@Mock
	private HubPort hubPort;

	@Mock
	private DeliveryDistributedLockService deliveryDistributedLockService;

	@InjectMocks
	private DeliveryCreateFacade deliveryCreateFacade;

	@Nested
	@DisplayName("배송 생성 실패")
	class CreateDeliveryFail {

		@Test
		@DisplayName("존재하지 않는 수령업체 ID")
		void createDelivery_fail_companyNotFound() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverId = UUID.randomUUID();
			CreateDeliveryCommand command = new CreateDeliveryCommand(
				orderId, sourceHubId, receiverCompanyId, receiverId,
				"서울시 강남구 테헤란로 123", "101호", 37.5, 127.0
			);

			given(companyPort.getCompany(receiverCompanyId))
				.willThrow(new DeliveryException(DeliveryErrorCode.COMPANY_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCreateFacade.createDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.COMPANY_NOT_FOUND);
		}

		@Test
		@DisplayName("존재하지 않는 허브 (경로 계산 실패)")
		void createDelivery_fail_hubNotFound() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = new CreateDeliveryCommand(
				orderId, sourceHubId, receiverCompanyId, receiverId,
				"서울시 강남구 테헤란로 123", "101호", 37.5, 127.0
			);

			given(companyPort.getCompany(receiverCompanyId))
				.willReturn(new CompanyResponse(receiverCompanyId, destinationHubId));
			given(hubPort.getHubRoute(sourceHubId, destinationHubId))
				.willThrow(new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCreateFacade.createDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.HUB_NOT_FOUND);
		}
	}
}
