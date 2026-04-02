package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {

	Optional<DeliveryManager> findNextHubDeliveryManager(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	Optional<DeliveryManager> findNextCompanyDeliveryManager(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	int findNextSequence();

	DeliveryManager save(DeliveryManager deliveryManager);
}
