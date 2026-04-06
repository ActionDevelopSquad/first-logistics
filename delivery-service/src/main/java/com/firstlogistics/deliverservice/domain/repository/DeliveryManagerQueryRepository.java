package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.projection.DeliveryManagerSummaryProjection;
import com.firstlogistics.deliverservice.domain.spec.DeliveryManagerSearchSpec;

import java.util.List;

public interface DeliveryManagerQueryRepository {

	List<DeliveryManagerSummaryProjection> findDeliveryManagers(DeliveryManagerSearchSpec spec);
}
