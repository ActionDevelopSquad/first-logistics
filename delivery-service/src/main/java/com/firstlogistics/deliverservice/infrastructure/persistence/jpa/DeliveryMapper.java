package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.entity.DeliveryRoute;
import com.firstlogistics.deliverservice.domain.vo.Address;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryRouteId;
import com.firstlogistics.deliverservice.domain.vo.DeliveryStaffId;
import com.firstlogistics.deliverservice.domain.vo.Distance;
import com.firstlogistics.deliverservice.domain.vo.GeoLocation;
import com.firstlogistics.deliverservice.domain.vo.Time;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeliveryMapper {

	public DeliveryJpaEntity toJpaEntity(Delivery delivery) {
		DeliveryJpaEntity jpaEntity = DeliveryJpaEntity.create(
			delivery.getId().id(),
			delivery.getOrderId(),
			delivery.getStatus(),
			delivery.getSourceHubId(),
			delivery.getDestinationHubId(),
			delivery.getDeliveryAddress().roadAddress(),
			delivery.getDeliveryAddress().detailAddress(),
			delivery.getDeliveryLocation().latitude(),
			delivery.getDeliveryLocation().longitude(),
			delivery.getReceiverId(),
			delivery.getReceiverSlackId(),
			delivery.getReceiverCompanyId(),
			delivery.getReceiverCompanyDeliveryStaffId().id(),
			delivery.getCurrentHubId()
		);

		for (DeliveryRoute route : delivery.getRoutes()) {
			DeliveryRouteJpaEntity routeJpaEntity = toRouteJpaEntity(route, jpaEntity);
			jpaEntity.addRoute(routeJpaEntity);
		}

		return jpaEntity;
	}

	private DeliveryRouteJpaEntity toRouteJpaEntity(DeliveryRoute route, DeliveryJpaEntity deliveryJpaEntity) {
		return DeliveryRouteJpaEntity.create(
			route.getId().id(),
			deliveryJpaEntity,
			route.getDeliveryRouteSequence(),
			route.getSourceHubId(),
			route.getDestinationHubId(),
			route.getEstimatedDistance().meters(),
			route.getEstimatedDuration().minutes(),
			route.getActualDistance().meters(),
			route.getActualDuration().minutes(),
			route.getStatus(),
			route.getDeliveryStaffId().id()
		);
	}

	public Delivery toDomain(DeliveryJpaEntity jpaEntity) {
		List<DeliveryRoute> routes = jpaEntity.getRoutes().stream()
			.map(this::toRouteDomain)
			.collect(Collectors.toCollection(ArrayList::new));

		return Delivery.reconstitute(
			DeliveryId.of(jpaEntity.getId()),
			jpaEntity.getOrderId(),
			jpaEntity.getStatus(),
			jpaEntity.getSourceHubId(),
			jpaEntity.getDestinationHubId(),
			Address.of(jpaEntity.getRoadAddress(), jpaEntity.getDetailAddress()),
			GeoLocation.of(jpaEntity.getLatitude(), jpaEntity.getLongitude()),
			jpaEntity.getReceiverId(),
			jpaEntity.getReceiverSlackId(),
			jpaEntity.getReceiverCompanyId(),
			DeliveryStaffId.of(jpaEntity.getReceiverCompanyDeliveryStaffId()),
			jpaEntity.getCurrentHubId(),
			routes
		);
	}

	private DeliveryRoute toRouteDomain(DeliveryRouteJpaEntity jpaEntity) {
		return DeliveryRoute.reconstitute(
			DeliveryRouteId.of(jpaEntity.getId()),
			DeliveryId.of(jpaEntity.getDelivery().getId()),
			jpaEntity.getDeliveryRouteSequence(),
			jpaEntity.getSourceHubId(),
			jpaEntity.getDestinationHubId(),
			Distance.of(jpaEntity.getEstimatedDistance()),
			Time.of(jpaEntity.getEstimatedDuration()),
			Distance.of(jpaEntity.getActualDistance()),
			Time.of(jpaEntity.getActualDuration()),
			null,
			null,
			jpaEntity.getStatus(),
			DeliveryStaffId.of(jpaEntity.getDeliveryStaffId())
		);
	}
}
