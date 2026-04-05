package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.UserRole;
import com.firstlogistics.deliverservice.domain.projection.DeliveryManagerSummaryProjection;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerQueryRepository;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;
import com.firstlogistics.deliverservice.domain.spec.DeliveryScope;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerQueryRepositoryImpl implements DeliveryManagerQueryRepository {

	private final JPAQueryFactory queryFactory;

	private static final QDeliveryManagerJpaEntity manager = QDeliveryManagerJpaEntity.deliveryManagerJpaEntity;

	@Override
	public List<DeliveryManagerSummaryProjection> findDeliveryManagers(DeliveryManagerSearchSpec spec) {
		return queryFactory
			.select(Projections.constructor(DeliveryManagerSummaryProjection.class,
				manager.id,
				manager.userId,
				manager.managerName,
				manager.phoneNumber,
				manager.hubId,
				manager.slackId,
				manager.managerType,
				manager.deliverySequence,
				manager.createdAt
			))
			.from(manager)
			.where(
				manager.deletedAt.isNull(),
				scopeCondition(spec.scope()),
				hubIdEq(spec),
				managerTypeEq(spec),
				managerNameContains(spec),
				phoneNumberContains(spec),
				cursorCondition(spec)
			)
			.orderBy(manager.createdAt.desc(), manager.id.desc())
			.limit(spec.size() + 1)
			.fetch();
	}

	private BooleanExpression scopeCondition(DeliveryScope scope) {
		if (scope == null) {
			return null;
		}
		return switch (scope.role()) {
			case HUB_MANAGER -> manager.hubId.eq(scope.scopeId());
			case DELIVERY_MANAGER -> manager.id.eq(scope.scopeId());
			default -> null;
		};
	}

	private BooleanExpression hubIdEq(DeliveryManagerSearchSpec spec) {
		return spec.hubId() != null ? manager.hubId.eq(spec.hubId()) : null;
	}

	private BooleanExpression managerTypeEq(DeliveryManagerSearchSpec spec) {
		return spec.managerType() != null ? manager.managerType.eq(spec.managerType()) : null;
	}

	private BooleanExpression managerNameContains(DeliveryManagerSearchSpec spec) {
		return spec.managerName() != null && !spec.managerName().isBlank()
			? manager.managerName.contains(spec.managerName()) : null;
	}

	private BooleanExpression phoneNumberContains(DeliveryManagerSearchSpec spec) {
		return spec.phoneNumber() != null && !spec.phoneNumber().isBlank()
			? manager.phoneNumber.contains(spec.phoneNumber()) : null;
	}

	private BooleanExpression cursorCondition(DeliveryManagerSearchSpec spec) {
		if (spec.cursorId() == null || spec.cursorCreatedAt() == null) {
			return null;
		}
		return manager.createdAt.lt(spec.cursorCreatedAt())
			.or(manager.createdAt.eq(spec.cursorCreatedAt()).and(manager.id.lt(spec.cursorId())));
	}
}
