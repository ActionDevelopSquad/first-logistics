package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.publisher.DeliveryEventPublisher;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubRouteStepResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryCommandServiceTest {

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private DeliveryStaffRepository deliveryStaffRepository;

	@Mock
	private UserPort userPort;

	@Mock
	private DeliveryEventPublisher deliveryEventPublisher;

	@InjectMocks
	private DeliveryCommandService deliveryCommandService;

	@Nested
	@DisplayName("배송 생성 실패")
	class CreateDeliveryFail {

		@Test
		@DisplayName("주문 ID가 이미 존재하는 배송")
		void createDelivery_fail_deliveryAlreadyExists() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(orderId, receiverCompanyId, receiverManagerId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId);
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(true);

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryCommandService.createDelivery(command, stubSupplierCompany(sourceHubId), receiverCompany, hubRoute));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ALREADY_EXISTS);
		}

		@Test
		@DisplayName("배정 가능한 허브 배송담당자 없음 (2번째 스텝에서 담당자 없음)")
		void createDelivery_fail_hubDeliveryStaffNotAvailable() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(orderId, receiverCompanyId, receiverManagerId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId);
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
			given(deliveryStaffRepository.findNextHubStaff(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubStaff(sourceHubId)));
			given(deliveryStaffRepository.findNextHubStaff(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryCommandService.createDelivery(command, stubSupplierCompany(sourceHubId), receiverCompany, hubRoute));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.HUB_DELIVERY_STAFF_NOT_AVAILABLE);
		}

		@Test
		@DisplayName("배정 가능한 업체 배송담당자 없음")
		void createDelivery_fail_companyDeliveryStaffNotAvailable() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(orderId, receiverCompanyId, receiverManagerId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId);
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
			given(deliveryStaffRepository.findNextHubStaff(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubStaff(sourceHubId)));
			given(deliveryStaffRepository.findNextHubStaff(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubStaff(middleHubId)));
			given(deliveryStaffRepository.findNextCompanyStaff(eq(destinationHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryCommandService.createDelivery(command, stubSupplierCompany(sourceHubId), receiverCompany, hubRoute));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.COMPANY_DELIVERY_STAFF_NOT_AVAILABLE);
		}

		@Test
		@DisplayName("존재하지 않는 수령인 ID")
		void createDelivery_fail_receiverNotFound() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(orderId, receiverCompanyId, receiverManagerId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId);
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
			given(deliveryStaffRepository.findNextHubStaff(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubStaff(sourceHubId)));
			given(deliveryStaffRepository.findNextHubStaff(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubStaff(middleHubId)));
			given(deliveryStaffRepository.findNextCompanyStaff(eq(destinationHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubCompanyStaff(destinationHubId)));
			given(userPort.getUser(receiverManagerId))
				.willThrow(new DeliveryException(DeliveryErrorCode.USER_NOT_FOUND));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryCommandService.createDelivery(command, stubSupplierCompany(sourceHubId), receiverCompany, hubRoute));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.USER_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("배송 생성 성공")
	class CreateDeliverySuccess {

		@Test
		@DisplayName("배송 생성 시 status = CREATED")
		void createDelivery_success_deliveryStatusIsCreated() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.CREATED);
		}

		@Test
		@DisplayName("배송 생성 시 전체 경로 일괄 생성")
		void createDelivery_success_allRoutesCreated() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getRoutes()).hasSize(f.hubSteps().size() + 1);
		}

		@Test
		@DisplayName("배송 생성 시 허브 배송담당자 순번 기준 배정")
		void createDelivery_success_hubStaffsAssignedToRoutesBySequence() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getRoutes().get(0).getDeliveryStaffId()).isEqualTo(f.hubStaff1().getId());
			assertThat(captor.getValue().getRoutes().get(1).getDeliveryStaffId()).isEqualTo(f.hubStaff2().getId());
		}

		@Test
		@DisplayName("배송 생성 시 업체 배송담당자 순번 기준 배정")
		void createDelivery_success_companyStaffAssignedToDelivery() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getReceiverCompanyDeliveryStaffId()).isEqualTo(f.companyStaff().getId());
		}

		@Test
		@DisplayName("배송 생성 시 허브 배송담당자 타임테이블 생성")
		void createDelivery_success_hubStaffTimetableCreated() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<DeliveryStaff> staffCaptor = ArgumentCaptor.forClass(DeliveryStaff.class);
			then(deliveryStaffRepository).should(times(f.hubSteps().size() + 1)).save(staffCaptor.capture());
			List<DeliveryStaff> savedStaffs = staffCaptor.getAllValues();
			assertThat(savedStaffs.get(0).getTimetables()).hasSize(1);
			assertThat(savedStaffs.get(0).getTimetables().get(0).getStatus()).isEqualTo(TimetableStatus.CREATED);
			assertThat(savedStaffs.get(1).getTimetables()).hasSize(1);
			assertThat(savedStaffs.get(1).getTimetables().get(0).getStatus()).isEqualTo(TimetableStatus.CREATED);
		}

		@Test
		@DisplayName("배송 생성 시 업체 배송담당자 타임테이블 생성")
		void createDelivery_success_companyStaffTimetableCreated() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<DeliveryStaff> staffCaptor = ArgumentCaptor.forClass(DeliveryStaff.class);
			then(deliveryStaffRepository).should(times(f.hubSteps().size() + 1)).save(staffCaptor.capture());
			DeliveryStaff savedCompanyStaff = staffCaptor.getAllValues().get(f.hubSteps().size());
			assertThat(savedCompanyStaff.getTimetables()).hasSize(1);
			assertThat(savedCompanyStaff.getTimetables().get(0).getStatus()).isEqualTo(TimetableStatus.CREATED);
		}
	}

	// --- 성공 테스트 공통 픽스처 ---

	private record SuccessFixture(
		CreateDeliveryCommand command,
		CompanyResponse supplierCompany,
		CompanyResponse receiverCompany,
		HubRouteResponse hubRoute,
		DeliveryStaff hubStaff1,
		DeliveryStaff hubStaff2,
		DeliveryStaff companyStaff,
		String receiverSlackId
	) {
		List<HubRouteStepResponse> hubSteps() {
			List<HubRouteStepResponse> all = hubRoute.routes();
			return all.subList(0, all.size() - 1);
		}

		static SuccessFixture create() {
			UUID orderId = UUID.randomUUID();
			UUID supplierCompanyId = UUID.randomUUID();
			UUID supplierManagerId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID supplierHubId = sourceHubId;
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();

			CreateDeliveryCommand command = new CreateDeliveryCommand(
				orderId,
				LocalDateTime.now(),
				LocalDateTime.now().plusDays(3),
				"빠른 배송 부탁드립니다.",
				supplierCompanyId,
				supplierManagerId,
				receiverCompanyId,
				receiverManagerId,
				"서울시 강남구 테헤란로 123",
				"101호",
				List.of(new CreateDeliveryCommand.OrderItemInfo("마른 오징어", 50, 10000))
			);
			CompanyResponse supplierCompany = new CompanyResponse(supplierCompanyId, supplierHubId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId);
			HubRouteResponse hubRoute = new HubRouteResponse(sourceHubId, destinationHubId, List.of(
				new HubRouteStepResponse(0, sourceHubId, middleHubId, 10000, 30),
				new HubRouteStepResponse(1, middleHubId, destinationHubId, 8000, 25),
				new HubRouteStepResponse(2, destinationHubId, UUID.randomUUID(), 5000, 20)
			));
			DeliveryStaff hubStaff1 = DeliveryStaff.create("허브담당1", "010-1111-1111", sourceHubId, "slack-hub1", StaffType.HUB_DELIVERY, 0);
			DeliveryStaff hubStaff2 = DeliveryStaff.create("허브담당2", "010-2222-2222", middleHubId, "slack-hub2", StaffType.HUB_DELIVERY, 1);
			DeliveryStaff companyStaff = DeliveryStaff.create("업체담당1", "010-3333-3333", destinationHubId, "slack-company", StaffType.COMPANY_DELIVERY, 0);

			return new SuccessFixture(command, supplierCompany, receiverCompany, hubRoute, hubStaff1, hubStaff2, companyStaff, "slack-receiver");
		}
	}

	private void setupSuccessMocks(SuccessFixture f) {
		UUID sourceHubId = f.supplierCompany().hubId();
		UUID middleHubId = f.hubRoute().routes().get(1).sourceHubId();
		UUID receiverManagerId = f.command().receiverManagerId();

		given(deliveryRepository.existsByOrderId(f.command().orderId())).willReturn(false);
		given(deliveryStaffRepository.findNextHubStaff(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(Optional.of(f.hubStaff1()));
		given(deliveryStaffRepository.findNextHubStaff(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(Optional.of(f.hubStaff2()));
		given(deliveryStaffRepository.findNextCompanyStaff(eq(f.receiverCompany().hubId()), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(Optional.of(f.companyStaff()));
		given(userPort.getUser(receiverManagerId))
			.willReturn(new UserResponse(receiverManagerId, "수령인", f.receiverSlackId()));
		given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));
		given(deliveryStaffRepository.save(any(DeliveryStaff.class))).willAnswer(inv -> inv.getArgument(0));
	}

	private CreateDeliveryCommand stubCommand(UUID orderId, UUID receiverCompanyId, UUID receiverManagerId) {
		return new CreateDeliveryCommand(
			orderId,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(3),
			"요청사항",
			UUID.randomUUID(),
			UUID.randomUUID(),
			receiverCompanyId,
			receiverManagerId,
			"서울시 강남구 테헤란로 123",
			"101호",
			List.of(new CreateDeliveryCommand.OrderItemInfo("마른 오징어", 50, 10000))
		);
	}

	private HubRouteResponse stubHubRoute(UUID sourceHubId, UUID middleHubId, UUID destinationHubId) {
		return new HubRouteResponse(sourceHubId, destinationHubId, List.of(
			new HubRouteStepResponse(0, sourceHubId, middleHubId, 10000, 30),
			new HubRouteStepResponse(1, middleHubId, destinationHubId, 8000, 25),
			new HubRouteStepResponse(2, destinationHubId, UUID.randomUUID(), 5000, 20)
		));
	}

	private CompanyResponse stubSupplierCompany(UUID sourceHubId) {
		return new CompanyResponse(UUID.randomUUID(), sourceHubId);
	}

	private DeliveryStaff stubHubStaff(UUID hubId) {
		return DeliveryStaff.create("홍길동", "010-1234-5678", hubId, "slack-hub", StaffType.HUB_DELIVERY, 0);
	}

	private DeliveryStaff stubCompanyStaff(UUID hubId) {
		return DeliveryStaff.create("김영희", "010-9876-5432", hubId, "slack-company", StaffType.COMPANY_DELIVERY, 0);
	}
}
