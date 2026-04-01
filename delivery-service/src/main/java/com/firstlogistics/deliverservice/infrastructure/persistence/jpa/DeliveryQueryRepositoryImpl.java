package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetail;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.port.DeliveryQueryRepositoryPort;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.firstlogistics.deliverservice.infrastructure.persistence.jpa.DeliveryQueryCondition.*;

@Repository
@RequiredArgsConstructor
public class DeliveryQueryRepositoryImpl implements DeliveryQueryRepositoryPort {

	private final JPAQueryFactory queryFactory;
	private final DeliveryJpaRepository deliveryJpaRepository;
	private final DeliveryMapper deliveryMapper;

	private static final QDeliveryJpaEntity delivery = QDeliveryJpaEntity.deliveryJpaEntity;
	private static final QDeliveryRouteJpaEntity route = QDeliveryRouteJpaEntity.deliveryRouteJpaEntity;
	private static final QDeliveryStaffJpaEntity hubDeliveryStaff = new QDeliveryStaffJpaEntity("hubDeliveryStaff");
	private static final QDeliveryStaffJpaEntity companyDeliveryStaff = new QDeliveryStaffJpaEntity("companyDeliveryStaff");
	private static final QStaffTimetableJpaEntity staffTimetable = QStaffTimetableJpaEntity.staffTimetableJpaEntity;

	@Override
	public Optional<DeliveryDetail> findById(UUID deliveryId) {
		DeliveryDetail base = queryFactory
			.select(Projections.constructor(DeliveryDetail.class,
				delivery.id,
				delivery.orderId,
				delivery.status,
				delivery.sourceHubId,
				delivery.destinationHubId,
				delivery.roadAddress,
				delivery.detailAddress,
				delivery.receiverId,
				delivery.receiverCompanyId,
				delivery.currentHubId,
				companyDeliveryStaff.staffName,
				companyDeliveryStaff.phoneNumber,
				delivery.createdAt
			))
			.from(delivery)
			.leftJoin(companyDeliveryStaff).on(companyDeliveryStaff.id.eq(delivery.receiverCompanyDeliveryStaffId))
			.where(delivery.id.eq(deliveryId), notDeleted())
			.fetchOne();

		return Optional.ofNullable(base);
	}

	@Override
	public List<DeliveryDetail.RouteDetail> findRoutesByDeliveryId(UUID deliveryId) {
		return queryFactory
			.select(Projections.constructor(DeliveryDetail.RouteDetail.class,
				route.id,
				route.deliveryRouteSequence,
				route.sourceHubId,
				route.destinationHubId,
				route.estimatedDistance,
				route.estimatedDuration,
				route.actualDistance,
				route.actualDuration,
				route.status,
				route.deliveryStaffId,
				hubDeliveryStaff.staffName,
				hubDeliveryStaff.phoneNumber,
				staffTimetable.expectedStartAt,
				staffTimetable.expectedEndAt
			))
			.from(route)
			.leftJoin(hubDeliveryStaff).on(hubDeliveryStaff.id.eq(route.deliveryStaffId))
			.leftJoin(staffTimetable).on(
				staffTimetable.deliveryStaff.id.eq(route.deliveryStaffId)
					.and(staffTimetable.deliveryId.eq(route.deliveryId))
			)
			.where(route.deliveryId.eq(deliveryId))
			.orderBy(route.deliveryRouteSequence.asc())
			.fetch();
	}

	@Override
	public List<DeliveryListResult.DeliverySummary> findDeliveries(DeliveryListQuery query) {
		return queryFactory
			.selectDistinct(Projections.constructor(DeliveryListResult.DeliverySummary.class,
				delivery.id,
				delivery.orderId,
				delivery.status,
				delivery.sourceHubId,
				delivery.destinationHubId,
				delivery.roadAddress,
				delivery.detailAddress,
				delivery.receiverCompanyId,
				delivery.currentHubId,
				delivery.createdAt
			))
			.from(delivery)
			.leftJoin(delivery.routes, route)
			.leftJoin(hubDeliveryStaff).on(hubDeliveryStaff.id.eq(route.deliveryStaffId))
			.leftJoin(companyDeliveryStaff).on(companyDeliveryStaff.id.eq(delivery.receiverCompanyDeliveryStaffId))
			.where(
				notDeleted(),
				scopeCondition(query),
				orderIdEq(query),
				statusEq(query),
				sourceHubEq(query),
				destinationHubEq(query),
				receiverCompanyIdEq(query),
				receiverIdEq(query),
				resolvedReceiverIdIn(query),
				staffNameContains(query, hubDeliveryStaff, companyDeliveryStaff),
				staffPhoneContains(query, hubDeliveryStaff, companyDeliveryStaff),
				dateRange(query),
				cursorCondition(query)
			)
			.orderBy(delivery.createdAt.desc(), delivery.id.desc())
			.limit(query.size() + 1L)
			.fetch();
	}
}
