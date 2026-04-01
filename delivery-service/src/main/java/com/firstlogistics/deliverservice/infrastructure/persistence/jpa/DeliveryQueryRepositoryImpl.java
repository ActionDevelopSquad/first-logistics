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
	public DeliveryListResult findDeliveries(DeliveryListQuery query) {
		List<DeliveryListResult.DeliverySummary> results = queryFactory
			.selectDistinct(Projections.constructor(DeliveryListResult.DeliverySummary.class,
				delivery.id,
				delivery.orderId,
				delivery.status,
				delivery.sourceHubId,
				delivery.destinationHubId,
				delivery.roadAddress,
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

		boolean hasNext = results.size() > query.resolvedSize();
		if (hasNext) {
			results = results.subList(0, query.resolvedSize());
		}

		return DeliveryListResult.of(results, hasNext);
	}

	private BooleanExpression scopeCondition(DeliveryListQuery query) {
		return switch (query.scope().role()) {
			case HUB_MANAGER -> scopeForHubManager(query.scope().scopeId());
			case DELIVERY_MANAGER -> scopeForDeliveryManager(query.scope().scopeId());
			case COMPANY_MANAGER -> scopeForCompanyManager(query.scope().scopeId());
			default -> null;
		};
	}
}
