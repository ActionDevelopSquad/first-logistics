package com.firstlogistics.deliverservice.application;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;
import com.firstlogistics.deliverservice.domain.spec.DeliverySearchSpec;
import com.firstlogistics.deliverservice.domain.projection.DeliveryDetailProjection;
import com.firstlogistics.deliverservice.domain.projection.DeliverySummaryProjection;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetailResult;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.application.port.CompanyPort;
import com.firstlogistics.deliverservice.domain.repository.DeliveryQueryRepository;
import com.firstlogistics.deliverservice.application.port.HubPort;
import com.firstlogistics.deliverservice.application.port.HubStaffPort;
import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.CompanyResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubResponse;
import com.firstlogistics.deliverservice.application.port.dto.HubStaffResponse;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.domain.enums.DeliveryStatus;
import com.firstlogistics.deliverservice.domain.enums.RouteStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryQueryServiceTest {

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private DeliveryQueryRepository deliveryQueryRepository;

	@Mock
	private UserPort userPort;

	@Mock
	private HubPort hubPort;

	@Mock
	private HubStaffPort hubStaffPort;

	@Mock
	private CompanyPort companyPort;

	@InjectMocks
	private DeliveryQueryService deliveryQueryService;

	@Nested
	@DisplayName("배송 목록 조회 실패")
	class GetDeliveriesFail {

		@Test
		@DisplayName("유효하지 않은 역할")
		void getDeliveries_fail_invalidRole() {
			// given & when
			Throwable throwable = catchThrowable(() -> new DeliveryListQuery(
				"INVALID_ROLE", null, null,
				null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, 10
			));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_QUERY_PARAMS);
		}

		@Test
		@DisplayName("null 역할")
		void getDeliveries_fail_nullRole() {
			// given & when
			Throwable throwable = catchThrowable(() -> new DeliveryListQuery(
				null, null, null,
				null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, 10
			));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_QUERY_PARAMS);
		}

		@Test
		@DisplayName("잘못된 날짜 범위 (endDate < startDate)")
		void getDeliveries_fail_invalidDateRange() {
			// given
			LocalDateTime startDate = LocalDateTime.now();
			LocalDateTime endDate = startDate.minusDays(1);

			// when
			Throwable throwable = catchThrowable(() -> stubQueryForMaster(startDate, endDate));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_DATE_RANGE);
		}

		@Test
		@DisplayName("스코프 필요한 역할인데 scopeId 없음 - HUB_MANAGER")
		void getDeliveries_fail_hubManagerWithoutScopeId() {
			// given & when
			Throwable throwable = catchThrowable(() ->
				DeliveryScope.of(UserRole.HUB_MANAGER, null));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.INVALID_ROLE_SCOPE);
		}

		@Test
		@DisplayName("수령인 이름으로 조회했지만 해당 유저 없음 → receiver 조건 없이 쿼리")
		void getDeliveries_receiverNotFound_queriesWithoutReceiverFilter() {
			// given
			DeliveryListQuery query = stubQueryWithReceiver(null, null, "없는사람", null);

			given(userPort.findByNameOrPhone("없는사람", null)).willReturn(List.of());
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of());

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).isEmpty();
		}
	}

	@Nested
	@DisplayName("배송 목록 조회 성공")
	class GetDeliveriesSuccess {

		@Test
		@DisplayName("전체 배송 목록 조회 - MASTER")
		void getDeliveries_success_masterReturnsDeliveries() {
			// given
			DeliveryListQuery query = stubQueryForMaster(null, null);

			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of(stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
		}

		@Test
		@DisplayName("담당 허브 배송 목록 조회 - HUB_MANAGER")
		void getDeliveries_success_hubManagerReturnsHubDeliveries() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID hubId = UUID.randomUUID();
			DeliveryListQuery query = stubQueryForRole(UserRole.HUB_MANAGER, managerId);

			given(hubStaffPort.getHubStaff(managerId)).willReturn(new HubStaffResponse(managerId, hubId));
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of(stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
		}

		@Test
		@DisplayName("본인 담당 배송 목록 조회 - DELIVERY_MANAGER")
		void getDeliveries_success_deliveryManagerReturnsOwnDeliveries() {
			// given
			DeliveryListQuery query = stubQueryForRole(UserRole.DELIVERY_MANAGER, UUID.randomUUID());

			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of(stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
		}

		@Test
		@DisplayName("본인 업체 관련 배송 목록 조회 - COMPANY_MANAGER")
		void getDeliveries_success_companyManagerReturnsCompanyDeliveries() {
			// given
			UUID managerId = UUID.randomUUID();
			UUID companyId = UUID.randomUUID();
			DeliveryListQuery query = stubQueryForRole(UserRole.COMPANY_MANAGER, managerId);

			given(companyPort.getCompanyByManagerId(managerId)).willReturn(new CompanyResponse(companyId, UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of(stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
		}

		@Test
		@DisplayName("수령인 이름으로 조회 - UserPort에서 receiverId resolve 후 쿼리")
		void getDeliveries_success_receiverNameFilter() {
			// given
			UUID receiverId = UUID.randomUUID();
			DeliveryListQuery query = stubQueryWithReceiver(null, null, "홍길동", null);

			given(userPort.findByNameOrPhone("홍길동", null))
				.willReturn(List.of(new UserResponse(receiverId, "홍길동", "010-1234-5678", "slack-123", "hong@test.com")));
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of(stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(1);
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
				UserRole.MASTER.name(), null, null,
				null, DeliveryStatus.HUB_MOVING, sourceHubId, destinationHubId,
				null, null, null, null, null, null, null,
				startDate, endDate, null, null, 10
			);
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class))).willReturn(List.of(stubSummary(), stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.deliveries()).hasSize(2);
		}

		@Test
		@DisplayName("커서 기반 다음 페이지 조회 - hasNext = true")
		void getDeliveries_success_cursorPaginationHasNext() {
			// given
			UUID cursorId = UUID.randomUUID();
			LocalDateTime cursorCreatedAt = LocalDateTime.now().minusHours(1);
			DeliveryListQuery query = new DeliveryListQuery(
				UserRole.MASTER.name(), null, null,
				null, null, null, null, null, null, null, null, null, null, null,
				null, null, cursorId, cursorCreatedAt, 10
			);
			List<DeliverySummaryProjection> elevenResults = java.util.stream.IntStream.range(0, 11)
				.mapToObj(i -> stubSummary())
				.toList();
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class)))
				.willReturn(elevenResults);

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
				UserRole.MASTER.name(), null, null,
				null, null, null, null, null, null, null, null, null, null, null,
				null, null, cursorId, cursorCreatedAt, 10
			);
			given(deliveryQueryRepository.findDeliveries(any(DeliverySearchSpec.class)))
				.willReturn(List.of(stubSummary()));

			// when
			DeliveryListResult result = deliveryQueryService.getDeliveries(query);

			// then
			assertThat(result.hasNext()).isFalse();
		}
	}

	@Nested
	@DisplayName("배송 상세 조회 실패")
	class GetDeliveryFail {

		@Test
		@DisplayName("존재하지 않는 배송 ID")
		void getDelivery_fail_deliveryNotFound() {
			// given
			UUID deliveryId = UUID.randomUUID();
			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.empty());

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryQueryService.getDelivery(deliveryId, UserRole.MASTER.name(), null));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_NOT_FOUND);
		}

		@Test
		@DisplayName("담당 허브의 배송이 아님 - HUB_MANAGER")
		void getDelivery_fail_hubManagerAccessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			UUID otherHubId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(List.of());
			given(hubStaffPort.getHubStaff(managerId)).willReturn(new HubStaffResponse(managerId, otherHubId));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryQueryService.getDelivery(deliveryId, UserRole.HUB_MANAGER.name(), managerId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("본인 담당 배송이 아님 - DELIVERY_MANAGER")
		void getDelivery_fail_deliveryManagerAccessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID otherStaffId = UUID.randomUUID();
			List<DeliveryDetailProjection.RouteDetail> routes = stubRoutes(2);
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(routes);

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryQueryService.getDelivery(deliveryId, UserRole.DELIVERY_MANAGER.name(), otherStaffId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}

		@Test
		@DisplayName("본인 업체 관련 배송이 아님 - COMPANY_MANAGER")
		void getDelivery_fail_companyManagerAccessDenied() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			UUID otherCompanyId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(List.of());
			given(companyPort.getCompanyByManagerId(managerId))
				.willReturn(new CompanyResponse(otherCompanyId, UUID.randomUUID(), "다른업체", "다른주소", "다른상세주소"));

			// when
			Throwable throwable = catchThrowable(() ->
				deliveryQueryService.getDelivery(deliveryId, UserRole.COMPANY_MANAGER.name(), managerId));
			log.info("throwable = {}", throwable.getMessage());

			// then
			assertThat(throwable)
				.isInstanceOf(DeliveryException.class)
				.hasFieldOrPropertyWithValue("errorCode", DeliveryErrorCode.DELIVERY_ACCESS_DENIED);
		}
	}

	@Nested
	@DisplayName("배송 상세 조회 성공")
	class GetDeliverySuccess {

		@Test
		@DisplayName("배송 상세 정보 반환 - MASTER")
		void getDelivery_success_returnsDeliveryDetailProjection() {
			// given
			UUID deliveryId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			List<DeliveryDetailProjection.RouteDetail> routes = stubRoutes(2);
			UUID receiverId = projection.receiverId();
			UUID receiverCompanyId = projection.receiverCompanyId();

			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(routes);
			given(hubPort.getHubs(any())).willReturn(stubHubResponses(projection, routes));
			given(userPort.getUser(receiverId)).willReturn(new UserResponse(receiverId, "홍길동", "010-1234-5678", "slack-123", "hong@test.com"));
			given(companyPort.getCompany(receiverCompanyId)).willReturn(new CompanyResponse(receiverCompanyId, UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));

			// when
			DeliveryDetailResult result = deliveryQueryService.getDelivery(deliveryId, UserRole.MASTER.name(), null);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
			assertThat(result.status()).isEqualTo(DeliveryStatus.CREATED);
			assertThat(result.receiver().name()).isEqualTo("홍길동");
			assertThat(result.receiverCompany().name()).isEqualTo("테스트업체");
			assertThat(result.routes()).hasSize(2);
		}

		@Test
		@DisplayName("담당 허브의 배송 상세 조회 허용 - HUB_MANAGER (sourceHub)")
		void getDelivery_success_hubManagerSourceHub() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			List<DeliveryDetailProjection.RouteDetail> routes = stubRoutes(1);

			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(routes);
			given(hubStaffPort.getHubStaff(managerId)).willReturn(new HubStaffResponse(managerId, projection.sourceHubId()));
			given(hubPort.getHubs(any())).willReturn(stubHubResponses(projection, routes));
			given(userPort.getUser(projection.receiverId())).willReturn(new UserResponse(projection.receiverId(), "홍길동", "010-1234-5678", "slack-123", "hong@test.com"));
			given(companyPort.getCompany(projection.receiverCompanyId())).willReturn(new CompanyResponse(projection.receiverCompanyId(), UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));

			// when
			DeliveryDetailResult result = deliveryQueryService.getDelivery(deliveryId, UserRole.HUB_MANAGER.name(), managerId);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}

		@Test
		@DisplayName("담당 허브의 배송 상세 조회 허용 - HUB_MANAGER (destinationHub)")
		void getDelivery_success_hubManagerDestinationHub() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			List<DeliveryDetailProjection.RouteDetail> routes = stubRoutes(1);

			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(routes);
			given(hubStaffPort.getHubStaff(managerId)).willReturn(new HubStaffResponse(managerId, projection.destinationHubId()));
			given(hubPort.getHubs(any())).willReturn(stubHubResponses(projection, routes));
			given(userPort.getUser(projection.receiverId())).willReturn(new UserResponse(projection.receiverId(), "홍길동", "010-1234-5678", "slack-123", "hong@test.com"));
			given(companyPort.getCompany(projection.receiverCompanyId())).willReturn(new CompanyResponse(projection.receiverCompanyId(), UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));

			// when
			DeliveryDetailResult result = deliveryQueryService.getDelivery(deliveryId, UserRole.HUB_MANAGER.name(), managerId);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}

		@Test
		@DisplayName("본인 담당 배송 상세 조회 허용 - DELIVERY_MANAGER")
		void getDelivery_success_deliveryManagerAssigned() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID staffId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			List<DeliveryDetailProjection.RouteDetail> routes = List.of(
				new DeliveryDetailProjection.RouteDetail(
					UUID.randomUUID(), 0,
					UUID.randomUUID(), UUID.randomUUID(),
					10000, 30, 0, 0,
					RouteStatus.CREATED,
					staffId,
					"담당자", "010-0000-0000",
					LocalDateTime.now(), LocalDateTime.now().plusHours(2)
				)
			);

			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(routes);
			given(hubPort.getHubs(any())).willReturn(stubHubResponses(projection, routes));
			given(userPort.getUser(projection.receiverId())).willReturn(new UserResponse(projection.receiverId(), "홍길동", "010-1234-5678", "slack-123", "hong@test.com"));
			given(companyPort.getCompany(projection.receiverCompanyId())).willReturn(new CompanyResponse(projection.receiverCompanyId(), UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));

			// when
			DeliveryDetailResult result = deliveryQueryService.getDelivery(deliveryId, UserRole.DELIVERY_MANAGER.name(), staffId);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}

		@Test
		@DisplayName("본인 업체 관련 배송 상세 조회 허용 - COMPANY_MANAGER")
		void getDelivery_success_companyManagerOwnsCompany() {
			// given
			UUID deliveryId = UUID.randomUUID();
			UUID managerId = UUID.randomUUID();
			DeliveryDetailProjection projection = stubProjection(deliveryId);
			List<DeliveryDetailProjection.RouteDetail> routes = stubRoutes(1);

			given(deliveryQueryRepository.findById(deliveryId)).willReturn(Optional.of(projection));
			given(deliveryQueryRepository.findRoutesByDeliveryId(deliveryId)).willReturn(routes);
			given(companyPort.getCompanyByManagerId(managerId)).willReturn(new CompanyResponse(projection.receiverCompanyId(), UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));
			given(hubPort.getHubs(any())).willReturn(stubHubResponses(projection, routes));
			given(userPort.getUser(projection.receiverId())).willReturn(new UserResponse(projection.receiverId(), "홍길동", "010-1234-5678", "slack-123", "hong@test.com"));
			given(companyPort.getCompany(projection.receiverCompanyId())).willReturn(new CompanyResponse(projection.receiverCompanyId(), UUID.randomUUID(), "테스트업체", "서울시 강남구 테헤란로 123", "101동 202호"));

			// when
			DeliveryDetailResult result = deliveryQueryService.getDelivery(deliveryId, UserRole.COMPANY_MANAGER.name(), managerId);

			// then
			assertThat(result.deliveryId()).isEqualTo(deliveryId);
		}
	}

	private DeliveryListQuery stubQueryForMaster(LocalDateTime startDate, LocalDateTime endDate) {
		return new DeliveryListQuery(
			UserRole.MASTER.name(), null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			startDate, endDate, null, null, 10
		);
	}

	private DeliveryListQuery stubQueryForRole(UserRole role, UUID userId) {
		return new DeliveryListQuery(
			role.name(), userId, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, 10
		);
	}

	private DeliveryListQuery stubQueryWithReceiver(LocalDateTime startDate, LocalDateTime endDate, String receiverName, String receiverPhone) {
		return new DeliveryListQuery(
			UserRole.MASTER.name(), null, null,
			null, null, null, null, null, null, null, receiverName, receiverPhone, null, null,
			startDate, endDate, null, null, 10
		);
	}

	private DeliverySummaryProjection stubSummary() {
		return new DeliverySummaryProjection(
			UUID.randomUUID(),
			UUID.randomUUID(),
			DeliveryStatus.CREATED,
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구 테헤란로 123",
			"101동 202호",
			UUID.randomUUID(),
			UUID.randomUUID(),
			LocalDateTime.now()
		);
	}

	private DeliveryDetailProjection stubProjection(UUID deliveryId) {
		return new DeliveryDetailProjection(
			deliveryId, UUID.randomUUID(), DeliveryStatus.CREATED,
			UUID.randomUUID(), UUID.randomUUID(),
			"서울시 강남구 테헤란로 123", "101동 202호",
			UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
			"업체담당자", "010-9999-9999",
			LocalDateTime.now()
		);
	}

	private List<DeliveryDetailProjection.RouteDetail> stubRoutes(int routeCount) {
		List<DeliveryDetailProjection.RouteDetail> routes = new ArrayList<>();
		for (int i = 0; i < routeCount; i++) {
			LocalDateTime startAt = LocalDateTime.now().plusHours(i);
			routes.add(new DeliveryDetailProjection.RouteDetail(
				UUID.randomUUID(), i,
				UUID.randomUUID(), UUID.randomUUID(),
				10000, 30, 0, 0,
				RouteStatus.CREATED,
				UUID.randomUUID(),
				"담당자" + i, "010-0000-000" + i,
				startAt, startAt.plusHours(2)
			));
		}
		return routes;
	}

	private List<HubResponse> stubHubResponses(DeliveryDetailProjection projection, List<DeliveryDetailProjection.RouteDetail> routes) {
		List<UUID> hubIds = new ArrayList<>();
		hubIds.add(projection.sourceHubId());
		hubIds.add(projection.destinationHubId());
		hubIds.add(projection.currentHubId());
		routes.forEach(route -> {
			hubIds.add(route.sourceHubId());
			hubIds.add(route.destinationHubId());
		});
		return hubIds.stream().distinct()
			.map(id -> new HubResponse(id, "허브-" + id.toString().substring(0, 4), "허브주소"))
			.toList();
	}
}
