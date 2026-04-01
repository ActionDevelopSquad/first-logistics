package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryDetail;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryQueryRepositoryPort {

	Optional<DeliveryDetail> findById(UUID deliveryId);

	List<DeliveryDetail.RouteDetail> findRoutesByDeliveryId(UUID deliveryId);

	List<DeliveryListResult.DeliverySummary> findDeliveries(DeliveryListQuery query);
}
