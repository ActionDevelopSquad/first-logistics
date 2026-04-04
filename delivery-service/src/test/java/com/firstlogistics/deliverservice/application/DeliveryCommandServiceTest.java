package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.command.ChangeDeliveryStatusCommand;
import com.firstlogistics.deliverservice.application.dto.command.CreateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.command.UpdateDeliveryCommand;
import com.firstlogistics.deliverservice.application.dto.result.ChangeDeliveryStatusResult;
import com.firstlogistics.deliverservice.application.dto.result.UpdateDeliveryResult;
import com.firstlogistics.deliverservice.application.publisher.DeliveryEventPublisher;
import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
import com.firstlogistics.deliverservice.domain.event.DeliveryStatusChangedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryUpdatedEvent;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryRouteId;
import com.firstlogistics.deliverservice.domain.vo.Distance;
import com.firstlogistics.deliverservice.domain.vo.Time;
import com.firstlogistics.deliverservice.application.permission.DeliveryPermissionValidator;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryCommandServiceTest {

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private DeliveryManagerRepository deliveryManagerRepository;

	@Mock
	private UserPort userPort;

	@Mock
	private HubPort hubPort;

	@Mock
	private DeliveryPermissionValidator deliveryPermissionValidator;

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
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호");
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
		void createDelivery_fail_hubDeliveryManagerNotAvailable() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(orderId, receiverCompanyId, receiverManagerId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호");
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
			given(deliveryManagerRepository.findNextHubDeliveryManager(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubManager(sourceHubId)));
			given(deliveryManagerRepository.findNextHubDeliveryManager(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryCommandService.createDelivery(command, stubSupplierCompany(sourceHubId), receiverCompany, hubRoute));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.HUB_DELIVERY_MANAGER_NOT_AVAILABLE);
		}

		@Test
		@DisplayName("배정 가능한 업체 배송담당자 없음")
		void createDelivery_fail_companyDeliveryManagerNotAvailable() {
			// given
			UUID orderId = UUID.randomUUID();
			UUID sourceHubId = UUID.randomUUID();
			UUID middleHubId = UUID.randomUUID();
			UUID receiverCompanyId = UUID.randomUUID();
			UUID receiverManagerId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			CreateDeliveryCommand command = stubCommand(orderId, receiverCompanyId, receiverManagerId);
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호");
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
			given(deliveryManagerRepository.findNextHubDeliveryManager(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubManager(sourceHubId)));
			given(deliveryManagerRepository.findNextHubDeliveryManager(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubManager(middleHubId)));
			given(deliveryManagerRepository.findNextCompanyDeliveryManager(eq(destinationHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryCommandService.createDelivery(command, stubSupplierCompany(sourceHubId), receiverCompany, hubRoute));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.COMPANY_DELIVERY_MANAGER_NOT_AVAILABLE);
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
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호");
			HubRouteResponse hubRoute = stubHubRoute(sourceHubId, middleHubId, destinationHubId);

			given(deliveryRepository.existsByOrderId(orderId)).willReturn(false);
			given(deliveryManagerRepository.findNextHubDeliveryManager(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubManager(sourceHubId)));
			given(deliveryManagerRepository.findNextHubDeliveryManager(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubHubManager(middleHubId)));
			given(deliveryManagerRepository.findNextCompanyDeliveryManager(eq(destinationHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
				.willReturn(Optional.of(stubCompanyManager(destinationHubId)));
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
		void createDelivery_success_hubDeliveryManagersAssignedToRoutesBySequence() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getRoutes().get(0).getDeliveryManagerId()).isEqualTo(f.hubDeliveryManager1().getId());
			assertThat(captor.getValue().getRoutes().get(1).getDeliveryManagerId()).isEqualTo(f.hubDeliveryManager2().getId());
		}

		@Test
		@DisplayName("배송 생성 시 업체 배송담당자 순번 기준 배정")
		void createDelivery_success_companyDeliveryManagerAssignedToDelivery() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getReceiverCompanyDeliveryManagerId()).isEqualTo(f.companyDeliveryManager().getId());
		}

		@Test
		@DisplayName("배송 생성 시 허브 배송담당자 타임테이블 생성")
		void createDelivery_success_hubManagerTimetableCreated() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<DeliveryManager> managerCaptor = ArgumentCaptor.forClass(DeliveryManager.class);
			then(deliveryManagerRepository).should(times(f.hubSteps().size() + 1)).save(managerCaptor.capture());
			List<DeliveryManager> savedManagers = managerCaptor.getAllValues();
			assertThat(savedManagers.get(0).getTimetables()).hasSize(1);
			assertThat(savedManagers.get(0).getTimetables().get(0).getStatus()).isEqualTo(TimetableStatus.CREATED);
			assertThat(savedManagers.get(1).getTimetables()).hasSize(1);
			assertThat(savedManagers.get(1).getTimetables().get(0).getStatus()).isEqualTo(TimetableStatus.CREATED);
		}

		@Test
		@DisplayName("배송 생성 시 업체 배송담당자 타임테이블 생성")
		void createDelivery_success_companyDeliveryManagerTimetableCreated() {
			// given
			SuccessFixture f = SuccessFixture.create();
			setupSuccessMocks(f);

			// when
			deliveryCommandService.createDelivery(f.command(), f.supplierCompany(), f.receiverCompany(), f.hubRoute());

			// then
			ArgumentCaptor<DeliveryManager> managerCaptor = ArgumentCaptor.forClass(DeliveryManager.class);
			then(deliveryManagerRepository).should(times(f.hubSteps().size() + 1)).save(managerCaptor.capture());
			DeliveryManager savedCompanyManager = managerCaptor.getAllValues().get(f.hubSteps().size());
			assertThat(savedCompanyManager.getTimetables()).hasSize(1);
			assertThat(savedCompanyManager.getTimetables().get(0).getStatus()).isEqualTo(TimetableStatus.CREATED);
		}
	}

	@Nested
	@DisplayName("배송 수정 실패")
	class UpdateDeliveryFail {

		@Test
		@DisplayName("수정할 필드가 없는 경우 (receiverId, receiverSlackId 모두 null)")
		void updateDelivery_fail_noFieldToUpdate() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			// when
			Throwable throwable = catchThrowable(() -> new UpdateDeliveryCommand(deliveryId, "MASTER", userId, null, null));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}

		@Test
		@DisplayName("수정할 필드가 없는 경우 (receiverSlackId 빈 문자열)")
		void updateDelivery_fail_noFieldToUpdate_blankSlackId() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			// when
			Throwable throwable = catchThrowable(() -> new UpdateDeliveryCommand(deliveryId, "MASTER", userId, null, "   "));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_COMMAND_PARAMS);
		}

		@Test
		@DisplayName("존재하지 않는 배송")
		void updateDelivery_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, UUID.randomUUID(), null);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.updateDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}

		@Test
		@DisplayName("권한 없음 (Validator가 DELIVERY_ACCESS_DENIED 던짐)")
		void updateDelivery_fail_accessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "COMPANY_MANAGER", userId, UUID.randomUUID(), null);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			willThrow(new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED))
				.given(deliveryPermissionValidator).validate(any(), eq("COMPANY_MANAGER"), eq(userId), any());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.updateDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("배송 중인 배송 수정 시도 (HUB_MOVING)")
		void updateDelivery_fail_deliveryInProgress() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.FOR_HUB_MOVING);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, UUID.randomUUID(), null);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.updateDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_MODIFIABLE);
		}

		@Test
		@DisplayName("이미 완료된 배송 수정 시도 (COMPLETED)")
		void updateDelivery_fail_deliveryCompleted() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.COMPLETED);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, UUID.randomUUID(), null);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.updateDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_MODIFIABLE);
		}

		@Test
		@DisplayName("이미 취소된 배송 수정 시도 (CANCELLED)")
		void updateDelivery_fail_deliveryCancelled() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CANCELLED);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, UUID.randomUUID(), null);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.updateDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_MODIFIABLE);
		}
	}

	@Nested
	@DisplayName("배송 수정 성공")
	class UpdateDeliverySuccess {

		@Test
		@DisplayName("수령인 ID 변경")
		void updateDelivery_success_receiverIdChanged() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID newReceiverId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, newReceiverId, null);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			UpdateDeliveryResult result = deliveryCommandService.updateDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getReceiverId()).isEqualTo(newReceiverId);
		}

		@Test
		@DisplayName("슬랙 ID 변경")
		void updateDelivery_success_slackIdChanged() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			String newSlackId = "new-slack-id";
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, null, newSlackId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			UpdateDeliveryResult result = deliveryCommandService.updateDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getReceiverSlackId()).isEqualTo(newSlackId);
		}

		@Test
		@DisplayName("수정 시 DeliveryUpdatedEvent 발행")
		void updateDelivery_success_deliveryUpdatedEventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			UUID newReceiverId = UUID.randomUUID();
			String newSlackId = "new-slack-id";
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			UpdateDeliveryCommand command = new UpdateDeliveryCommand(deliveryId, "MASTER", userId, newReceiverId, newSlackId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			UpdateDeliveryResult result = deliveryCommandService.updateDelivery(command);
			assertThat(result.deliveryId()).isEqualTo(deliveryId);

			// then
			ArgumentCaptor<DeliveryUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryUpdatedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryUpdated(eventCaptor.capture());
			DeliveryUpdatedEvent publishedEvent = eventCaptor.getValue();
			assertThat(publishedEvent.deliveryId()).isEqualTo(deliveryId);
			assertThat(publishedEvent.receiverId()).isEqualTo(newReceiverId);
			assertThat(publishedEvent.receiverSlackId()).isEqualTo(newSlackId);
		}
	}

	// ===== 배송 상태 변경 테스트 =====

	@Nested
	@DisplayName("허브 배송 시작 실패")
	class StartHubDeliveryFail {

		@Test
		@DisplayName("존재하지 않는 배송")
		void startHubDelivery_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startHubDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}

		@Test
		@DisplayName("권한 없음 (COMPANY_MANAGER)")
		void startHubDelivery_fail_accessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "COMPANY_MANAGER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			willThrow(new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED))
				.given(deliveryPermissionValidator).validate(any(), eq("COMPANY_MANAGER"), eq(userId), any());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startHubDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("잘못된 상태 (FOR_HUB_MOVING)")
		void startHubDelivery_fail_invalidStatus() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.FOR_HUB_MOVING);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startHubDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}

	@Nested
	@DisplayName("허브 배송 시작 성공")
	class StartHubDeliverySuccess {

		@Test
		@DisplayName("CREATED 상태에서 시작")
		void startHubDelivery_success_fromCreated() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.startHubDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.FOR_HUB_MOVING);
		}

		@Test
		@DisplayName("HUB_WAITING 상태에서 시작 (경유지 출발)")
		void startHubDelivery_success_fromHubWaiting() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithMultipleRoutes(deliveryId, DeliveryStatus.HUB_WAITING);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.startHubDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.FOR_HUB_MOVING);
		}

		@Test
		@DisplayName("시작 시 경로 상태 변경 및 이벤트 발행")
		void startHubDelivery_success_routeStatusAndEvent() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.startHubDelivery(command);

			// then
			assertThat(delivery.getRoutes().get(0).getStatus()).isEqualTo(RouteStatus.MOVING);
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			DeliveryStatusChangedEvent captured = eventCaptor.getValue();
			assertThat(captured.status()).isEqualTo("FOR_HUB_MOVING");
			assertThat(captured.deliveryId()).isEqualTo(deliveryId);
			assertThat(captured.sourceHubId()).isEqualTo(STUB_SOURCE_HUB_ID);
			assertThat(captured.destinationHubId()).isEqualTo(STUB_DESTINATION_HUB_ID);
			assertThat(captured.routes()).hasSize(1);
		}
	}

	@Nested
	@DisplayName("허브 도착 실패")
	class ArriveHubFail {

		@Test
		@DisplayName("존재하지 않는 배송")
		void arriveHub_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.arriveHub(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}

		@Test
		@DisplayName("잘못된 상태 (HUB_WAITING)")
		void arriveHub_fail_invalidStatus() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.HUB_WAITING);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.arriveHub(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}

	@Nested
	@DisplayName("허브 도착 성공")
	class ArriveHubSuccess {

		@Test
		@DisplayName("중간 허브 도착 → HUB_ARRIVED + currentHubId 업데이트")
		void arriveHub_success_intermediateHub() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubMovingDeliveryWithMultipleRoutes(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.arriveHub(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			Delivery saved = captor.getValue();
			assertThat(saved.getStatus()).isEqualTo(DeliveryStatus.HUB_ARRIVED);
			assertThat(saved.getCurrentHubId()).isEqualTo(STUB_MIDDLE_HUB_ID);
		}

		@Test
		@DisplayName("최종 허브 도착 → HUB_ARRIVED + currentHubId 업데이트")
		void arriveHub_success_finalHub() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubMovingDeliveryLastRoute(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.arriveHub(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			Delivery saved = captor.getValue();
			assertThat(saved.getStatus()).isEqualTo(DeliveryStatus.HUB_ARRIVED);
			assertThat(saved.getCurrentHubId()).isEqualTo(STUB_DESTINATION_HUB_ID);
		}

		@Test
		@DisplayName("허브 도착 시 이벤트 발행")
		void arriveHub_success_eventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubMovingDeliveryLastRoute(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.arriveHub(command);

			// then
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			DeliveryStatusChangedEvent captured = eventCaptor.getValue();
			assertThat(captured.status()).isEqualTo("HUB_ARRIVED");
			assertThat(captured.deliveryId()).isEqualTo(deliveryId);
			assertThat(captured.routes()).hasSize(2);
		}
	}

	@Nested
	@DisplayName("입고 완료 실패")
	class ReceiveAtHubFail {

		@Test
		@DisplayName("잘못된 상태 (HUB_WAITING)")
		void receiveAtHub_fail_invalidStatus() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.HUB_WAITING);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.receiveAtHub(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}

	@Nested
	@DisplayName("입고 완료 성공")
	class ReceiveAtHubSuccess {

		@Test
		@DisplayName("HUB_ARRIVED → HUB_WAITING")
		void receiveAtHub_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.HUB_ARRIVED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.receiveAtHub(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.HUB_WAITING);
		}

		@Test
		@DisplayName("입고 완료 시 이벤트 발행")
		void receiveAtHub_success_eventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.HUB_ARRIVED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.receiveAtHub(command);

			// then
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			DeliveryStatusChangedEvent captured = eventCaptor.getValue();
			assertThat(captured.status()).isEqualTo("HUB_WAITING");
			assertThat(captured.deliveryId()).isEqualTo(deliveryId);
		}

	}

	@Nested
	@DisplayName("업체 배송 시작 실패")
	class StartCompanyDeliveryFail {

		@Test
		@DisplayName("존재하지 않는 배송")
		void startCompanyDelivery_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startCompanyDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}

		@Test
		@DisplayName("권한 없음 (COMPANY_MANAGER)")
		void startCompanyDelivery_fail_accessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryAtFinalHub(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "COMPANY_MANAGER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			willThrow(new DeliveryException(DeliveryErrorCode.DELIVERY_ACCESS_DENIED))
				.given(deliveryPermissionValidator).validate(any(), eq("COMPANY_MANAGER"), eq(userId), any());

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startCompanyDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("잘못된 상태 (CREATED)")
		void startCompanyDelivery_fail_invalidStatus() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startCompanyDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}

		@Test
		@DisplayName("마지막 허브가 아닌데 업체 배송 시작 시도")
		void startCompanyDelivery_fail_notAtFinalHub() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithMultipleRoutes(deliveryId, DeliveryStatus.HUB_WAITING);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.startCompanyDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.NOT_COMPANY_DELIVERY_PHASE);
		}
	}

	@Nested
	@DisplayName("업체 배송 시작 성공")
	class StartCompanyDeliverySuccess {

		@Test
		@DisplayName("HUB_WAITING + 마지막 허브 → FOR_COMPANY_MOVING")
		void startCompanyDelivery_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryAtFinalHub(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.startCompanyDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.FOR_COMPANY_MOVING);
		}

		@Test
		@DisplayName("업체 배송 시작 시 이벤트 발행")
		void startCompanyDelivery_success_eventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryAtFinalHub(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.startCompanyDelivery(command);

			// then
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			DeliveryStatusChangedEvent captured = eventCaptor.getValue();
			assertThat(captured.status()).isEqualTo("FOR_COMPANY_MOVING");
			assertThat(captured.deliveryId()).isEqualTo(deliveryId);
		}
	}

	@Nested
	@DisplayName("배송 완료 실패")
	class CompleteDeliveryFail {

		@Test
		@DisplayName("잘못된 상태 (HUB_ARRIVED)")
		void completeDelivery_fail_invalidStatus() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.HUB_ARRIVED);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.completeDelivery(command));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}

	@Nested
	@DisplayName("배송 완료 성공")
	class CompleteDeliverySuccess {

		@Test
		@DisplayName("FOR_COMPANY_MOVING → COMPLETED")
		void completeDelivery_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubCompanyMovingDelivery(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.completeDelivery(command);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.COMPLETED);
		}

		@Test
		@DisplayName("배송 완료 시 이벤트 발행")
		void completeDelivery_success_eventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();
			Delivery delivery = stubCompanyMovingDelivery(deliveryId);
			ChangeDeliveryStatusCommand command = ChangeDeliveryStatusCommand.of(deliveryId, "MASTER", userId);

			given(deliveryRepository.findById(DeliveryId.of(deliveryId))).willReturn(Optional.of(delivery));
			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.completeDelivery(command);

			// then
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			DeliveryStatusChangedEvent captured = eventCaptor.getValue();
			assertThat(captured.status()).isEqualTo("COMPLETED");
			assertThat(captured.deliveryId()).isEqualTo(deliveryId);
		}
	}

	// ===== 배송 취소 테스트 =====

	@Nested
	@DisplayName("배송 취소 실패")
	class CancelDeliveryFail {

		@Test
		@DisplayName("CREATED 아닌 상태 (FOR_HUB_MOVING)")
		void cancelDelivery_fail_invalidStatus() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.FOR_HUB_MOVING);

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.cancelDelivery(delivery));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}

	@Nested
	@DisplayName("배송 취소 성공")
	class CancelDeliverySuccess {

		@Test
		@DisplayName("CREATED → CANCELLED")
		void cancelDelivery_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			ChangeDeliveryStatusResult result = deliveryCommandService.cancelDelivery(delivery);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
		}

		@Test
		@DisplayName("취소 시 이벤트 발행")
		void cancelDelivery_success_eventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.cancelDelivery(delivery);

			// then
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			DeliveryStatusChangedEvent captured = eventCaptor.getValue();
			assertThat(captured.status()).isEqualTo("CANCELLED");
			assertThat(captured.deliveryId()).isEqualTo(deliveryId);
		}
	}

	// ===== 시스템 배송 취소 테스트 =====

	@Nested
	@DisplayName("시스템 배송 취소 실패")
	class CancelDeliveryBySystemFail {

		@Test
		@DisplayName("배송 출발 후 취소 불가")
		void cancelDeliveryBySystem_fail_invalidStatusTransition() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.FOR_HUB_MOVING);

			// when
			Throwable throwable = catchThrowable(() -> deliveryCommandService.cancelDeliveryBySystem(delivery));

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_STATUS_TRANSITION);
		}
	}

	@Nested
	@DisplayName("시스템 배송 취소 성공")
	class CancelDeliveryBySystemSuccess {

		@Test
		@DisplayName("CREATED 상태에서 취소 성공")
		void cancelDeliveryBySystem_success() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.cancelDeliveryBySystem(delivery);

			// then
			ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
			then(deliveryRepository).should().save(captor.capture());
			assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
		}

		@Test
		@DisplayName("취소 시 이벤트 발행")
		void cancelDeliveryBySystem_success_eventPublished() {
			// given
			UUID deliveryId = UUID.randomUUID();
			Delivery delivery = stubDeliveryWithRoutes(deliveryId, DeliveryStatus.CREATED);

			given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			deliveryCommandService.cancelDeliveryBySystem(delivery);

			// then
			ArgumentCaptor<DeliveryStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(DeliveryStatusChangedEvent.class);
			then(deliveryEventPublisher).should().publishDeliveryStatusChanged(eventCaptor.capture());
			assertThat(eventCaptor.getValue().deliveryId()).isEqualTo(deliveryId);
			assertThat(eventCaptor.getValue().status()).isEqualTo("CANCELLED");
		}
	}

	// --- 배송 수정 테스트 픽스처 ---

	private static final UUID STUB_SOURCE_HUB_ID = UUID.randomUUID();
	private static final UUID STUB_MIDDLE_HUB_ID = UUID.randomUUID();
	private static final UUID STUB_DESTINATION_HUB_ID = UUID.randomUUID();
	private static final DeliveryManagerId STUB_ROUTE_MANAGER_ID = DeliveryManagerId.generate();

	private Delivery stubDeliveryWithRoutes(UUID deliveryId, DeliveryStatus status) {
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

	private Delivery stubDeliveryWithMultipleRoutes(UUID deliveryId, DeliveryStatus status) {
		DeliveryId id = DeliveryId.of(deliveryId);
		DeliveryRoute route1 = DeliveryRoute.reconstitute(
			DeliveryRouteId.generate(), id, 0,
			STUB_SOURCE_HUB_ID, STUB_MIDDLE_HUB_ID,
			Distance.of(10000), Time.of(30), null, null, null, null,
			RouteStatus.ARRIVED, STUB_ROUTE_MANAGER_ID
		);
		DeliveryRoute route2 = DeliveryRoute.create(id, 1, STUB_MIDDLE_HUB_ID, STUB_DESTINATION_HUB_ID, 8000, 25);
		route2.assignManager(STUB_ROUTE_MANAGER_ID);
		return Delivery.reconstitute(
			id, UUID.randomUUID(), status,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Address.of("서울시 강남구 테헤란로 123", "101호"),
			null,
			UUID.randomUUID(), "slack-receiver", UUID.randomUUID(),
			DeliveryManagerId.generate(), STUB_MIDDLE_HUB_ID,
			List.of(route1, route2)
		);
	}

	private Delivery stubMovingDeliveryWithMultipleRoutes(UUID deliveryId) {
		DeliveryId id = DeliveryId.of(deliveryId);
		DeliveryRoute route1 = DeliveryRoute.reconstitute(
			DeliveryRouteId.generate(), id, 0,
			STUB_SOURCE_HUB_ID, STUB_MIDDLE_HUB_ID,
			Distance.of(10000), Time.of(30), null, null, null, null,
			RouteStatus.MOVING, STUB_ROUTE_MANAGER_ID
		);
		DeliveryRoute route2 = DeliveryRoute.create(id, 1, STUB_MIDDLE_HUB_ID, STUB_DESTINATION_HUB_ID, 8000, 25);
		route2.assignManager(STUB_ROUTE_MANAGER_ID);
		return Delivery.reconstitute(
			id, UUID.randomUUID(), DeliveryStatus.FOR_HUB_MOVING,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Address.of("서울시 강남구 테헤란로 123", "101호"),
			null,
			UUID.randomUUID(), "slack-receiver", UUID.randomUUID(),
			DeliveryManagerId.generate(), STUB_SOURCE_HUB_ID,
			List.of(route1, route2)
		);
	}

	private Delivery stubMovingDeliveryLastRoute(UUID deliveryId) {
		DeliveryId id = DeliveryId.of(deliveryId);
		DeliveryRoute route1 = DeliveryRoute.reconstitute(
			DeliveryRouteId.generate(), id, 0,
			STUB_SOURCE_HUB_ID, STUB_MIDDLE_HUB_ID,
			Distance.of(10000), Time.of(30), null, null, null, null,
			RouteStatus.ARRIVED, STUB_ROUTE_MANAGER_ID
		);
		DeliveryRoute route2 = DeliveryRoute.reconstitute(
			DeliveryRouteId.generate(), id, 1,
			STUB_MIDDLE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Distance.of(8000), Time.of(25), null, null, null, null,
			RouteStatus.MOVING, STUB_ROUTE_MANAGER_ID
		);
		return Delivery.reconstitute(
			id, UUID.randomUUID(), DeliveryStatus.FOR_HUB_MOVING,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Address.of("서울시 강남구 테헤란로 123", "101호"),
			null,
			UUID.randomUUID(), "slack-receiver", UUID.randomUUID(),
			DeliveryManagerId.generate(), STUB_MIDDLE_HUB_ID,
			List.of(route1, route2)
		);
	}

	private Delivery stubCompanyMovingDelivery(UUID deliveryId) {
		DeliveryId id = DeliveryId.of(deliveryId);
		DeliveryRoute route = DeliveryRoute.reconstitute(
			DeliveryRouteId.generate(), id, 0,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Distance.of(10000), Time.of(30), null, null, null, null,
			RouteStatus.MOVING, STUB_ROUTE_MANAGER_ID
		);
		return Delivery.reconstitute(
			id, UUID.randomUUID(), DeliveryStatus.FOR_COMPANY_MOVING,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Address.of("서울시 강남구 테헤란로 123", "101호"),
			null,
			UUID.randomUUID(), "slack-receiver", UUID.randomUUID(),
			DeliveryManagerId.generate(), STUB_DESTINATION_HUB_ID,
			List.of(route)
		);
	}

	private Delivery stubDeliveryAtFinalHub(UUID deliveryId) {
		DeliveryId id = DeliveryId.of(deliveryId);
		DeliveryRoute hubRoute = DeliveryRoute.reconstitute(
			DeliveryRouteId.generate(), id, 0,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Distance.of(10000), Time.of(30), null, null, null, null,
			RouteStatus.ARRIVED, STUB_ROUTE_MANAGER_ID
		);
		DeliveryRoute companyRoute = DeliveryRoute.create(id, 1, STUB_DESTINATION_HUB_ID, UUID.randomUUID(), 5000, 20);
		companyRoute.assignManager(STUB_ROUTE_MANAGER_ID);
		return Delivery.reconstitute(
			id, UUID.randomUUID(), DeliveryStatus.HUB_WAITING,
			STUB_SOURCE_HUB_ID, STUB_DESTINATION_HUB_ID,
			Address.of("서울시 강남구 테헤란로 123", "101호"),
			null,
			UUID.randomUUID(), "slack-receiver", UUID.randomUUID(),
			DeliveryManagerId.generate(), STUB_DESTINATION_HUB_ID,
			List.of(hubRoute, companyRoute)
		);
	}

	// --- 성공 테스트 공통 픽스처 ---

	private record SuccessFixture(
		CreateDeliveryCommand command,
		CompanyResponse supplierCompany,
		CompanyResponse receiverCompany,
		HubRouteResponse hubRoute,
		DeliveryManager hubDeliveryManager1,
		DeliveryManager hubDeliveryManager2,
		DeliveryManager companyDeliveryManager,
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
			UUID hubManager1UserId = UUID.randomUUID();
			UUID hubManager2UserId = UUID.randomUUID();
			UUID companyManagerUserId = UUID.randomUUID();

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
				List.of(new CreateDeliveryCommand.OrderItemInfo(UUID.randomUUID(), "마른 오징어", 50, 10000L))
			);
			CompanyResponse supplierCompany = new CompanyResponse(supplierCompanyId, supplierHubId, "공급업체", "서울시 송파구 올림픽로 300", "A동 1층");
			CompanyResponse receiverCompany = new CompanyResponse(receiverCompanyId, destinationHubId, "수령업체", "서울시 강남구 테헤란로 123", "101동 202호");
			HubRouteResponse hubRoute = new HubRouteResponse(sourceHubId, destinationHubId, List.of(
				new HubRouteStepResponse(0, sourceHubId, middleHubId, 10000, 30),
				new HubRouteStepResponse(1, middleHubId, destinationHubId, 8000, 25),
				new HubRouteStepResponse(2, destinationHubId, UUID.randomUUID(), 5000, 20)
			));
			DeliveryManager hubDeliveryManager1 = DeliveryManager.create(hubManager1UserId, "허브담당1", "010-1111-1111", sourceHubId, "slack-hub1", ManagerType.HUB_DELIVERY, 0);
			DeliveryManager hubDeliveryManager2 = DeliveryManager.create(hubManager2UserId, "허브담당2", "010-2222-2222", middleHubId, "slack-hub2", ManagerType.HUB_DELIVERY, 1);
			DeliveryManager companyDeliveryManager = DeliveryManager.create(companyManagerUserId, "업체담당1", "010-3333-3333", destinationHubId, "slack-company", ManagerType.COMPANY_DELIVERY, 0);

			return new SuccessFixture(command, supplierCompany, receiverCompany, hubRoute, hubDeliveryManager1, hubDeliveryManager2, companyDeliveryManager, "slack-receiver");
		}
	}

	private void setupSuccessMocks(SuccessFixture f) {
		UUID sourceHubId = f.supplierCompany().hubId();
		UUID middleHubId = f.hubRoute().routes().get(1).sourceHubId();
		UUID receiverManagerId = f.command().receiverManagerId();

		given(deliveryRepository.existsByOrderId(f.command().orderId())).willReturn(false);
		given(deliveryManagerRepository.findNextHubDeliveryManager(eq(sourceHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(Optional.of(f.hubDeliveryManager1()));
		given(deliveryManagerRepository.findNextHubDeliveryManager(eq(middleHubId), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(Optional.of(f.hubDeliveryManager2()));
		given(deliveryManagerRepository.findNextCompanyDeliveryManager(eq(f.receiverCompany().hubId()), any(LocalDateTime.class), any(LocalDateTime.class)))
			.willReturn(Optional.of(f.companyDeliveryManager()));
		given(userPort.getUser(receiverManagerId))
			.willReturn(new UserResponse(receiverManagerId, "수령인", "010-0000-0000", f.receiverSlackId(), "receiver@test.com"));
		given(deliveryRepository.save(any(Delivery.class))).willAnswer(inv -> inv.getArgument(0));
		given(deliveryManagerRepository.save(any(DeliveryManager.class))).willAnswer(inv -> inv.getArgument(0));
		given(hubPort.getHubs(any())).willReturn(
			f.hubRoute().routes().stream()
				.flatMap(step -> java.util.stream.Stream.of(step.sourceHubId(), step.destinationHubId()))
				.distinct()
				.map(id -> new HubResponse(id, "허브-" + id.toString().substring(0, 4), "허브주소-" + id.toString().substring(0, 4)))
				.toList()
		);
		given(userPort.getUser(f.companyDeliveryManager().getUserId()))
			.willReturn(new UserResponse(f.companyDeliveryManager().getUserId(), "업체담당1", "010-3333-3333", "slack-company", "company-manager@test.com"));
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
			List.of(new CreateDeliveryCommand.OrderItemInfo(UUID.randomUUID(), "마른 오징어", 50, 10000L))
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
		return new CompanyResponse(UUID.randomUUID(), sourceHubId, "공급업체", "서울시 송파구 올림픽로 300", "A동 1층");
	}

	private DeliveryManager stubHubManager(UUID hubId) {
		return DeliveryManager.create(UUID.randomUUID(), "홍길동", "010-1234-5678", hubId, "slack-hub", ManagerType.HUB_DELIVERY, 0);
	}

	private DeliveryManager stubCompanyManager(UUID hubId) {
		return DeliveryManager.create(UUID.randomUUID(), "김영희", "010-9876-5432", hubId, "slack-company", ManagerType.COMPANY_DELIVERY, 0);
	}
}
