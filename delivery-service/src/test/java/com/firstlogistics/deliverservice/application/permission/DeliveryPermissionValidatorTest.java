package com.firstlogistics.deliverservice.application.permission;

import com.firstlogistics.deliverservice.application.permission.strategy.CompanyManagerPermissionStrategy;
import com.firstlogistics.deliverservice.application.permission.strategy.DeliveryManagerPermissionStrategy;
import com.firstlogistics.deliverservice.application.permission.strategy.HubManagerPermissionStrategy;
import com.firstlogistics.deliverservice.application.permission.strategy.MasterPermissionStrategy;
import com.firstlogistics.deliverservice.application.permission.strategy.RolePermissionStrategy;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.application.port.HubManagerPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubManagerResponse;
import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import common.security.entity.enums.UserRole;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryPermissionValidatorTest {

	@Mock
	private HubManagerPort hubManagerPort;

	@Mock
	private CompanyPort companyPort;

	@Mock
	private DeliveryManagerRepository deliveryManagerRepository;

	private DeliveryPermissionValidator validator;

	@BeforeEach
	void setUp() {
		List<RolePermissionStrategy> strategies = List.of(
			new MasterPermissionStrategy(),
			new HubManagerPermissionStrategy(hubManagerPort),
			new DeliveryManagerPermissionStrategy(deliveryManagerRepository),
			new CompanyManagerPermissionStrategy(companyPort)
		);
		validator = new DeliveryPermissionValidator(strategies);
	}

	private static final UUID SOURCE_HUB_ID = UUID.randomUUID();
	private static final UUID DESTINATION_HUB_ID = UUID.randomUUID();
	private static final UUID RECEIVER_COMPANY_ID = UUID.randomUUID();
	private static final UUID ROUTE_MANAGER_ID = UUID.randomUUID();

	private DeliveryAccessContext stubContext() {
		return new DeliveryAccessContext(
			SOURCE_HUB_ID, DESTINATION_HUB_ID, RECEIVER_COMPANY_ID, List.of(ROUTE_MANAGER_ID)
		);
	}

	@Nested
	@DisplayName("MASTER 권한 검증")
	class MasterValidation {

		@Test
		@DisplayName("MASTER는 무조건 통과")
		void validate_success_masterAlwaysPasses() {
			// given
			DeliveryAccessContext context = stubContext();

			// when & then (예외 없음)
			validator.validate(context, UserRole.MASTER, UUID.randomUUID());
		}
	}

	@Nested
	@DisplayName("HUB_MANAGER 권한 검증")
	class HubManagerValidation {

		@Test
		@DisplayName("담당 허브가 sourceHub인 경우 통과")
		void validate_success_sourceHub() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryAccessContext context = stubContext();
			given(hubManagerPort.getHubManager(userId)).willReturn(new HubManagerResponse(userId, SOURCE_HUB_ID));

			// when & then (예외 없음)
			validator.validate(context, UserRole.HUB_MANAGER, userId);
		}

		@Test
		@DisplayName("담당 허브가 destinationHub인 경우 통과")
		void validate_success_destinationHub() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryAccessContext context = stubContext();
			given(hubManagerPort.getHubManager(userId)).willReturn(new HubManagerResponse(userId, DESTINATION_HUB_ID));

			// when & then (예외 없음)
			validator.validate(context, UserRole.HUB_MANAGER, userId);
		}

		@Test
		@DisplayName("담당 허브가 아닌 경우 거부")
		void validate_fail_notResponsibleHub() {
			// given
			UUID userId = UUID.randomUUID();
			UUID otherHubId = UUID.randomUUID();
			DeliveryAccessContext context = stubContext();
			given(hubManagerPort.getHubManager(userId)).willReturn(new HubManagerResponse(userId, otherHubId));

			// when
			Throwable throwable = catchThrowable(() -> validator.validate(context, UserRole.HUB_MANAGER, userId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}

	@Nested
	@DisplayName("DELIVERY_MANAGER 권한 검증")
	class DeliveryManagerValidation {

		@Test
		@DisplayName("본인 담당 배송인 경우 통과")
		void validate_success_assignedManager() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryManager manager = DeliveryManager.create(userId, "담당자", "010-0000-0000", UUID.randomUUID(), "slack", ManagerType.HUB_DELIVERY, 0);
			DeliveryAccessContext context = new DeliveryAccessContext(
				SOURCE_HUB_ID, DESTINATION_HUB_ID, RECEIVER_COMPANY_ID, List.of(manager.getId().id())
			);
			given(deliveryManagerRepository.findByUserId(userId)).willReturn(Optional.of(manager));

			// when & then (예외 없음)
			validator.validate(context, UserRole.DELIVERY_MANAGER, userId);
		}

		@Test
		@DisplayName("본인 담당이 아닌 경우 거부")
		void validate_fail_notAssigned() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryManager otherManager = DeliveryManager.create(userId, "다른담당자", "010-0000-0000", UUID.randomUUID(), "slack", ManagerType.HUB_DELIVERY, 0);
			DeliveryAccessContext context = stubContext();
			given(deliveryManagerRepository.findByUserId(userId)).willReturn(Optional.of(otherManager));

			// when
			Throwable throwable = catchThrowable(() -> validator.validate(context, UserRole.DELIVERY_MANAGER, userId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("배송 담당자를 찾을 수 없는 경우")
		void validate_fail_managerNotFound() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryAccessContext context = stubContext();
			given(deliveryManagerRepository.findByUserId(userId)).willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() -> validator.validate(context, UserRole.DELIVERY_MANAGER, userId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("COMPANY_MANAGER 권한 검증")
	class CompanyManagerValidation {

		@Test
		@DisplayName("본인 업체 관련 배송인 경우 통과")
		void validate_success_ownsCompany() {
			// given
			UUID userId = UUID.randomUUID();
			DeliveryAccessContext context = stubContext();
			given(companyPort.getCompanyManager(userId))
				.willReturn(new CompanyResponse(RECEIVER_COMPANY_ID, UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));

			// when & then (예외 없음)
			validator.validate(context, UserRole.COMPANY_MANAGER, userId);
		}

		@Test
		@DisplayName("본인 업체 관련이 아닌 경우 거부")
		void validate_fail_notOwnCompany() {
			// given
			UUID userId = UUID.randomUUID();
			UUID otherCompanyId = UUID.randomUUID();
			DeliveryAccessContext context = stubContext();
			given(companyPort.getCompanyManager(userId))
				.willReturn(new CompanyResponse(otherCompanyId, UUID.randomUUID(), "다른업체", "다른주소", "다른상세주소"));

			// when
			Throwable throwable = catchThrowable(() -> validator.validate(context, UserRole.COMPANY_MANAGER, userId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}

}
