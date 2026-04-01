package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;
import com.firstlogistics.deliverservice.application.port.DeliveryQueryRepositoryPort;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.firstlogistics.deliverservice.infrastructure.persistence.jpa.DeliveryQueryCondition.*;

@Repository
@RequiredArgsConstructor
public class DeliveryQueryRepositoryImpl implements DeliveryQueryRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private static final QDeliveryJpaEntity delivery = QDeliveryJpaEntity.deliveryJpaEntity;
	private static final QDeliveryRouteJpaEntity route = QDeliveryRouteJpaEntity.deliveryRouteJpaEntity;
	private static final QDeliveryStaffJpaEntity routeStaff = new QDeliveryStaffJpaEntity("routeStaff");
	private static final QDeliveryStaffJpaEntity companyStaff = new QDeliveryStaffJpaEntity("companyStaff");

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
			.leftJoin(routeStaff).on(routeStaff.id.eq(route.deliveryStaffId))
			.leftJoin(companyStaff).on(companyStaff.id.eq(delivery.receiverCompanyDeliveryStaffId))
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
				staffNameContains(query, routeStaff, companyStaff),
				staffPhoneContains(query, routeStaff, companyStaff),
				dateRange(query),
				cursorCondition(query)
			)
			.orderBy(delivery.createdAt.desc(), delivery.id.desc())
			.limit(query.resolvedSize() + 1L)
			.fetch();
	}
}
