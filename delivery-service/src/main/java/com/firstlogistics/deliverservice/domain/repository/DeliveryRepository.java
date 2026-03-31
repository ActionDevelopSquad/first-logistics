package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.Delivery;

import java.util.UUID;

public interface DeliveryRepository {

	boolean existsByOrderId(UUID orderId);

	Delivery save(Delivery delivery);
}
