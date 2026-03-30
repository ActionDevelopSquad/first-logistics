package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.port.DeliveryEventProducer;
import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import com.firstlogistics.deliverservice.infrastructure.feign.CompanyClient;
import com.firstlogistics.deliverservice.infrastructure.feign.HubClient;
import com.firstlogistics.deliverservice.infrastructure.feign.UserClient;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.CompanyResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.FeignResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryCommandServiceTest {

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private DeliveryStaffRepository deliveryStaffRepository;

	@Mock
	private CompanyClient companyClient;

	@Mock
	private HubClient hubClient;

	@Mock
	private UserClient userClient;

	@Mock
	private DeliveryEventProducer deliveryEventProducer;

	@InjectMocks
	private DeliveryCommandService deliveryCommandService;

	@Test
	@DisplayName("실패 - 주문 ID가 이미 존재하는 배송")
	void createDelivery_fail_deliveryAlreadyExists() {
		// given
		UUID orderId = UUID.randomUUID();
		UUID sourceHubId = UUID.randomUUID();
		UUID receiverCompanyId = UUID.randomUUID();
		UUID receiverId = UUID.randomUUID();
		CreateDeliveryCommand command = new CreateDeliveryCommand(
			orderId, sourceHubId, receiverCompanyId, receiverId,
			"서울시 강남구 테헤란로 123", "101호", 37.5, 127.0
		);

		given(deliveryRepository.existsByOrderId(orderId)).willReturn(true);

		// when
		Throwable throwable = catchThrowable(() -> deliveryCommandService.createDelivery(command));
		log.info("throwable = {}", throwable.getMessage());

		// then
		assertThat(throwable)
			.isInstanceOf(DeliveryException.class)
			.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ALREADY_EXISTS);
	}

	@Test
	@DisplayName("실패 - 존재하지 않는 수령업체 ID")
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

		given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
		given(companyClient.getCompany(receiverCompanyId))
			.willThrow(new DeliveryException(DeliveryErrorCode.COMPANY_NOT_FOUND));

		// when
		Throwable throwable = catchThrowable(() -> deliveryCommandService.createDelivery(command));
		log.info("throwable = {}", throwable.getMessage());

		// then
		assertThat(throwable)
			.isInstanceOf(DeliveryException.class)
			.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.COMPANY_NOT_FOUND);
	}

	@Test
	@DisplayName("실패 - 존재하지 않는 허브 (경로 계산 실패)")
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

		given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
		given(companyClient.getCompany(receiverCompanyId))
			.willReturn(new FeignResponse<>(new CompanyResponse(receiverCompanyId, destinationHubId)));
		given(hubClient.getHubRoute(sourceHubId, destinationHubId))
			.willThrow(new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND));

		// when
		Throwable throwable = catchThrowable(() -> deliveryCommandService.createDelivery(command));
		log.info("throwable = {}", throwable.getMessage());

		// then
		assertThat(throwable)
			.isInstanceOf(DeliveryException.class)
			.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.HUB_NOT_FOUND);
	}

	@Test
	@DisplayName("실패 - 배정 가능한 허브 배송담당자 없음 (2번째 스텝에서 담당자 없음)")
	void createDelivery_fail_hubDeliveryStaffNotAvailable() {
		// given
		UUID orderId = UUID.randomUUID();
		UUID sourceHubId = UUID.randomUUID();
		UUID middleHubId = UUID.randomUUID();
		UUID receiverCompanyId = UUID.randomUUID();
		UUID receiverId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		CreateDeliveryCommand command = new CreateDeliveryCommand(
			orderId, sourceHubId, receiverCompanyId, receiverId,
			"서울시 강남구 테헤란로 123", "101호", 37.5, 127.0
		);

		List<HubRouteStepResponse> steps = List.of(
			new HubRouteStepResponse(sourceHubId, middleHubId, 10000, 30),
			new HubRouteStepResponse(middleHubId, destinationHubId, 8000, 25)
		);

		given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
		given(companyClient.getCompany(receiverCompanyId))
			.willReturn(new FeignResponse<>(new CompanyResponse(receiverCompanyId, destinationHubId)));
		given(hubClient.getHubRoute(sourceHubId, destinationHubId))
			.willReturn(new FeignResponse<>(new HubRouteResponse(sourceHubId, destinationHubId, steps)));
		given(deliveryStaffRepository.findNextHubStaff(sourceHubId))
			.willReturn(Optional.of(stubHubStaff(sourceHubId)));
		given(deliveryStaffRepository.findNextHubStaff(middleHubId))
			.willReturn(Optional.empty());

		// when
		Throwable throwable = catchThrowable(() -> deliveryCommandService.createDelivery(command));
		log.info("throwable = {}", throwable.getMessage());

		// then
		assertThat(throwable)
			.isInstanceOf(DeliveryException.class)
			.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE);
	}

	@Test
	@DisplayName("실패 - 배정 가능한 업체 배송담당자 없음")
	void createDelivery_fail_companyDeliveryStaffNotAvailable() {
		// given
		UUID orderId = UUID.randomUUID();
		UUID sourceHubId = UUID.randomUUID();
		UUID middleHubId = UUID.randomUUID();
		UUID receiverCompanyId = UUID.randomUUID();
		UUID receiverId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		CreateDeliveryCommand command = new CreateDeliveryCommand(
			orderId, sourceHubId, receiverCompanyId, receiverId,
			"서울시 강남구 테헤란로 123", "101호", 37.5, 127.0
		);

		List<HubRouteStepResponse> steps = List.of(
			new HubRouteStepResponse(sourceHubId, middleHubId, 10000, 30),
			new HubRouteStepResponse(middleHubId, destinationHubId, 8000, 25)
		);

		given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
		given(companyClient.getCompany(receiverCompanyId))
			.willReturn(new FeignResponse<>(new CompanyResponse(receiverCompanyId, destinationHubId)));
		given(hubClient.getHubRoute(sourceHubId, destinationHubId))
			.willReturn(new FeignResponse<>(new HubRouteResponse(sourceHubId, destinationHubId, steps)));
		given(deliveryStaffRepository.findNextHubStaff(sourceHubId))
			.willReturn(Optional.of(stubHubStaff(sourceHubId)));
		given(deliveryStaffRepository.findNextHubStaff(middleHubId))
			.willReturn(Optional.of(stubHubStaff(middleHubId)));
		given(deliveryStaffRepository.findNextCompanyStaff(destinationHubId))
			.willReturn(Optional.empty());

		// when
		Throwable throwable = catchThrowable(() -> deliveryCommandService.createDelivery(command));
		log.info("throwable = {}", throwable.getMessage());

		// then
		assertThat(throwable)
			.isInstanceOf(DeliveryException.class)
			.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.COMPANY_DELIVERY_STAFF_NOT_AVAILABLE);
	}

	@Test
	@DisplayName("실패 - 존재하지 않는 수령인 ID")
	void createDelivery_fail_receiverNotFound() {
		// given
		UUID orderId = UUID.randomUUID();
		UUID sourceHubId = UUID.randomUUID();
		UUID middleHubId = UUID.randomUUID();
		UUID receiverCompanyId = UUID.randomUUID();
		UUID receiverId = UUID.randomUUID();
		UUID destinationHubId = UUID.randomUUID();
		CreateDeliveryCommand command = new CreateDeliveryCommand(
			orderId, sourceHubId, receiverCompanyId, receiverId,
			"서울시 강남구 테헤란로 123", "101호", 37.5, 127.0
		);

		List<HubRouteStepResponse> steps = List.of(
			new HubRouteStepResponse(sourceHubId, middleHubId, 10000, 30),
			new HubRouteStepResponse(middleHubId, destinationHubId, 8000, 25)
		);

		given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
		given(companyClient.getCompany(receiverCompanyId))
			.willReturn(new FeignResponse<>(new CompanyResponse(receiverCompanyId, destinationHubId)));
		given(hubClient.getHubRoute(sourceHubId, destinationHubId))
			.willReturn(new FeignResponse<>(new HubRouteResponse(sourceHubId, destinationHubId, steps)));
		given(deliveryStaffRepository.findNextHubStaff(sourceHubId))
			.willReturn(Optional.of(stubHubStaff(sourceHubId)));
		given(deliveryStaffRepository.findNextHubStaff(middleHubId))
			.willReturn(Optional.of(stubHubStaff(middleHubId)));
		given(deliveryStaffRepository.findNextCompanyStaff(destinationHubId))
			.willReturn(Optional.of(stubCompanyStaff(destinationHubId)));
		given(userClient.getUser(receiverId))
			.willThrow(new DeliveryException(DeliveryErrorCode.USER_NOT_FOUND));

		// when
		Throwable throwable = catchThrowable(() -> deliveryCommandService.createDelivery(command));
		log.info("throwable = {}", throwable.getMessage());

		// then
		assertThat(throwable)
			.isInstanceOf(DeliveryException.class)
			.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.USER_NOT_FOUND);
	}

	private DeliveryStaff stubHubStaff(UUID hubId) {
		UUID staffId = UUID.randomUUID();
		return DeliveryStaff.create(
			staffId, "홍길동", "010-1234-5678",
			hubId, "slack-hub",
			StaffType.HUB_DELIVERY, 0
		);
	}

	private DeliveryStaff stubCompanyStaff(UUID hubId) {
		UUID staffId = UUID.randomUUID();
		return DeliveryStaff.create(
			staffId, "김영희", "010-9876-5432",
			hubId, "slack-company",
			StaffType.COMPANY_DELIVERY, 0
		);
	}
}
