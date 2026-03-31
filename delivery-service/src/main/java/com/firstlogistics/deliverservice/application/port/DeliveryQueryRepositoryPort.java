package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.dto.query.DeliveryListQuery;
import com.firstlogistics.deliverservice.application.dto.result.DeliveryListResult;

public interface DeliveryQueryRepositoryPort {

	DeliveryListResult findDeliveries(DeliveryListQuery query);
}
