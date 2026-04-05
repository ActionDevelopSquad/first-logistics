package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {

	Optional<DeliveryManager> findById(DeliveryManagerId id);

	Optional<DeliveryManager> findNextHubDeliveryManager(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	Optional<DeliveryManager> findNextCompanyDeliveryManager(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	int findNextSequence();

	boolean existsByUserId(UUID userId);

	Optional<DeliveryManager> findByUserId(UUID userId);

	DeliveryManager save(DeliveryManager deliveryManager);
}
