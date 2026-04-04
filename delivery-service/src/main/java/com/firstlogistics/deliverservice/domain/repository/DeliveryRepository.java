package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

	boolean existsByOrderId(UUID orderId);

	Optional<Delivery> findById(DeliveryId deliveryId);

	Optional<Delivery> findByOrderId(UUID orderId);

	Delivery save(Delivery delivery);

	void deleteById(DeliveryId deliveryId, UUID userId);
}
