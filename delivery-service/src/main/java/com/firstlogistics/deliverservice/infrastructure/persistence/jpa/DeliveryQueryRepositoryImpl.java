package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.projection.DeliveryDetailProjection;
import com.firstlogistics.deliverservice.domain.projection.DeliverySummaryProjection;
import com.firstlogistics.deliverservice.domain.repository.DeliveryQueryRepository;
import com.firstlogistics.deliverservice.domain.spec.DeliverySearchSpec;
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
public class DeliveryQueryRepositoryImpl implements DeliveryQueryRepository {

	private final JPAQueryFactory queryFactory;

	private static final QDeliveryJpaEntity delivery = QDeliveryJpaEntity.deliveryJpaEntity;
	private static final QDeliveryRouteJpaEntity route = QDeliveryRouteJpaEntity.deliveryRouteJpaEntity;
	private static final QDeliveryStaffJpaEntity hubDeliveryStaff = new QDeliveryStaffJpaEntity("hubDeliveryStaff");
	private static final QDeliveryStaffJpaEntity companyDeliveryStaff = new QDeliveryStaffJpaEntity("companyDeliveryStaff");
	private static final QStaffTimetableJpaEntity staffTimetable = QStaffTimetableJpaEntity.staffTimetableJpaEntity;

	@Override
	public Optional<DeliveryDetailProjection> findById(UUID deliveryId) {
		DeliveryDetailProjection base = queryFactory
			.select(Projections.constructor(DeliveryDetailProjection.class,
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
	public List<DeliveryDetailProjection.RouteDetail> findRoutesByDeliveryId(UUID deliveryId) {
		return queryFactory
			.select(Projections.constructor(DeliveryDetailProjection.RouteDetail.class,
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
	public List<DeliverySummaryProjection> findDeliveries(DeliverySearchSpec spec) {
		return queryFactory
			.selectDistinct(Projections.constructor(DeliverySummaryProjection.class,
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
				scopeCondition(spec),
				orderIdEq(spec),
				statusEq(spec),
				sourceHubEq(spec),
				destinationHubEq(spec),
				receiverCompanyIdEq(spec),
				receiverIdEq(spec),
				resolvedReceiverIdIn(spec),
				staffNameContains(spec, hubDeliveryStaff, companyDeliveryStaff),
				staffPhoneContains(spec, hubDeliveryStaff, companyDeliveryStaff),
				dateRange(spec),
				cursorCondition(spec)
			)
			.orderBy(delivery.createdAt.desc(), delivery.id.desc())
			.limit(spec.size() + 1L)
			.fetch();
	}
}
