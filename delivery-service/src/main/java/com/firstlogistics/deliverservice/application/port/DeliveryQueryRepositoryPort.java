package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;

import java.util.List;

public interface DeliveryQueryRepositoryPort {

	List<DeliveryListResult.DeliverySummary> findDeliveries(DeliveryListQuery query);
}
