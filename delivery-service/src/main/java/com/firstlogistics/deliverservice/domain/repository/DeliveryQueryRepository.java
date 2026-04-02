package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.projection.DeliveryDetailProjection;
import com.firstlogistics.deliverservice.domain.projection.DeliverySummaryProjection;
import com.firstlogistics.deliverservice.domain.spec.DeliverySearchSpec;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryQueryRepository {

	Optional<DeliveryDetailProjection> findById(UUID deliveryId);

	List<DeliveryDetailProjection.RouteDetail> findRoutesByDeliveryId(UUID deliveryId);

	List<DeliverySummaryProjection> findDeliveries(DeliverySearchSpec spec);
}
