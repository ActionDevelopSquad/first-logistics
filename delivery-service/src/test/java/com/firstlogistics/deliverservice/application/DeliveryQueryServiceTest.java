package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.query.DeliveryScope;
import com.firstlogistics.deliverservice.application.enums.UserRole;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.port.DeliveryQueryRepositoryPort;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryQueryServiceTest {

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private DeliveryQueryRepositoryPort deliveryQueryRepositoryPort;

	@InjectMocks
	private DeliveryQueryService deliveryQueryService;

	@Nested
	@DisplayName("배송 목록 조회 실패")
	class GetDeliveriesFail {

		@Test
		@DisplayName("잘못된 날짜 범위 (endDate < startDate)")
		void getDeliveries_fail_invalidDateRange() {
			// given
			LocalDateTime startDate = LocalDateTime.now();
			LocalDateTime endDate = startDate.minusDays(1);
			DeliveryListQuery query = stubQueryForMaster(startDate, endDate);

			// when
			Throwable throwable = catchThrowable(() -> deliveryQueryService.getDeliveries(query));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_DATE_RANGE);
		}

		@Test
		@DisplayName("스코프 필요한 역할인데 scopeId 없음 - HUB_MANAGER")
		void getDeliveries_fail_hubManagerWithoutScopeId() {
			// given
			DeliveryListQuery query = stubQueryWithScope(DeliveryScope.of(UserRole.HUB_MANAGER, null));

			// when
			Throwable throwable = catchThrowable(() -> deliveryQueryService.getDeliveries(query));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_ROLE_SCOPE);
		}

		@Test
		@DisplayName("스코프 필요한 역할인데 scopeId 없음 - DELIVERY_MANAGER")
		void getDeliveries_fail_deliveryManagerWithoutScopeId() {
			// given
			DeliveryListQuery query = stubQueryWithScope(DeliveryScope.of(UserRole.DELIVERY_MANAGER, null));

			// when
			Throwable throwable = catchThrowable(() -> deliveryQueryService.getDeliveries(query));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_ROLE_SCOPE);
		}

		@Test
		@DisplayName("스코프 필요한 역할인데 scopeId 없음 - COMPANY_MANAGER")
		void getDeliveries_fail_companyManagerWithoutScopeId() {
			// given
			DeliveryListQuery query = stubQueryWithScope(DeliveryScope.of(UserRole.COMPANY_MANAGER, null));

			// when
			Throwable throwable = catchThrowable(() -> deliveryQueryService.getDeliveries(query));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_ROLE_SCOPE);
		}
	}

	@Nested
	@DisplayName("배송 목록 조회 성공")
	class GetDeliveriesSuccess {

		@Test
		@DisplayName("전체 배송 목록 조회 (빈 목록) - MASTER")
		void getDeliveries_success_masterReturnsEmptyList() {
			// given
			DeliveryListQuery query = stubQueryForMaster(null, null);
			DeliveryListResult expected = DeliveryListResult.of(List.of(), false);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).isEmpty();
			assertThat(result.hasNext()).isFalse();
		}

		@Test
		@DisplayName("담당 허브 배송 목록 조회 - HUB_MANAGER")
		void getDeliveries_success_hubManagerReturnsHubDeliveries() {
			// given
			UUID scopeHubId = UUID.randomUUID();
			DeliveryListQuery query = stubQueryForHubManager(scopeHubId);
			DeliveryListResult expected = DeliveryListResult.of(List.of(stubSummary()), false);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
			then(deliveryQueryRepositoryPort).should().findDeliveries(query);
		}

		@Test
		@DisplayName("본인 담당 배송 목록 조회 - DELIVERY_MANAGER")
		void getDeliveries_success_deliveryManagerReturnsOwnDeliveries() {
			// given
			UUID scopeStaffId = UUID.randomUUID();
			DeliveryListQuery query = stubQueryForDeliveryManager(scopeStaffId);
			DeliveryListResult expected = DeliveryListResult.of(List.of(stubSummary()), false);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
			then(deliveryQueryRepositoryPort).should().findDeliveries(query);
		}

		@Test
		@DisplayName("본인 업체 관련 배송 목록 조회 - COMPANY_MANAGER")
		void getDeliveries_success_companyManagerReturnsCompanyDeliveries() {
			// given
			UUID scopeCompanyId = UUID.randomUUID();
			DeliveryListQuery query = stubQueryForCompanyManager(scopeCompanyId);
			DeliveryListResult expected = DeliveryListResult.of(List.of(stubSummary()), false);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
			then(deliveryQueryRepositoryPort).should().findDeliveries(query);
		}

		@Test
		@DisplayName("배송 상태 / 출발지 허브 / 목적지 허브 / 날짜 범위 필터 조회")
		void getDeliveries_success_withFilters() {
			// given
			UUID sourceHubId = UUID.randomUUID();
			UUID destinationHubId = UUID.randomUUID();
			LocalDateTime startDate = LocalDateTime.now().minusDays(7);
			LocalDateTime endDate = LocalDateTime.now();
			DeliveryListQuery query = new DeliveryListQuery(
				DeliveryScope.of(UserRole.MASTER, null),
				DeliveryStatus.HUB_MOVING, sourceHubId, destinationHubId, null,
				startDate, endDate,
				null, null, 10
			);
			DeliveryListResult expected = DeliveryListResult.of(List.of(stubSummary(), stubSummary()), false);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(2);
			then(deliveryQueryRepositoryPort).should().findDeliveries(query);
		}

		@Test
		@DisplayName("커서 기반 다음 페이지 조회 - hasNext = true")
		void getDeliveries_success_cursorPaginationHasNext() {
			// given
			UUID cursorId = UUID.randomUUID();
			LocalDateTime cursorCreatedAt = LocalDateTime.now().minusHours(1);
			DeliveryListQuery query = new DeliveryListQuery(
				DeliveryScope.of(UserRole.MASTER, null),
				null, null, null, null,
				null, null,
				cursorId, cursorCreatedAt, 10
			);
			DeliveryListResult expected = DeliveryListResult.of(List.of(stubSummary()), true);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.hasNext()).isTrue();
		}

		@Test
		@DisplayName("마지막 페이지 조회 - hasNext = false")
		void getDeliveries_success_lastPage() {
			// given
			UUID cursorId = UUID.randomUUID();
			LocalDateTime cursorCreatedAt = LocalDateTime.now().minusHours(1);
			DeliveryListQuery query = new DeliveryListQuery(
				DeliveryScope.of(UserRole.MASTER, null),
				null, null, null, null,
				null, null,
				cursorId, cursorCreatedAt, 10
			);
			DeliveryListResult expected = DeliveryListResult.of(List.of(stubSummary()), false);

			given(deliveryQueryRepositoryPort.findDeliveries(query)).willReturn(expected);

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.hasNext()).isFalse();
		}
	}

	private DeliveryListQuery stubQueryForMaster(LocalDateTime startDate, LocalDateTime endDate) {
		return new DeliveryListQuery(
			DeliveryScope.of(UserRole.MASTER, null),
			null, null, null, null,
			startDate, endDate,
			null, null, 10
		);
	}

	private DeliveryListQuery stubQueryForHubManager(UUID scopeHubId) {
		return new DeliveryListQuery(
			DeliveryScope.of(UserRole.HUB_MANAGER, scopeHubId),
			null, null, null, null,
			null, null,
			null, null, 10
		);
	}

	private DeliveryListQuery stubQueryForDeliveryManager(UUID scopeStaffId) {
		return new DeliveryListQuery(
			DeliveryScope.of(UserRole.DELIVERY_MANAGER, scopeStaffId),
			null, null, null, null,
			null, null,
			null, null, 10
		);
	}

	private DeliveryListQuery stubQueryForCompanyManager(UUID scopeCompanyId) {
		return new DeliveryListQuery(
			DeliveryScope.of(UserRole.COMPANY_MANAGER, scopeCompanyId),
			null, null, null, null,
			null, null,
			null, null, 10
		);
	}

	private DeliveryListQuery stubQueryWithScope(DeliveryScope scope) {
		return new DeliveryListQuery(
			scope,
			null, null, null, null,
			null, null,
			null, null, 10
		);
	}

	private DeliveryListResult.DeliverySummary stubSummary() {
		return new DeliveryListResult.DeliverySummary(
			UUID.randomUUID(),
			UUID.randomUUID(),
			DeliveryStatus.CREATED,
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구 테헤란로 123",
			UUID.randomUUID(),
			UUID.randomUUID(),
			LocalDateTime.now()
		);
	}
}
