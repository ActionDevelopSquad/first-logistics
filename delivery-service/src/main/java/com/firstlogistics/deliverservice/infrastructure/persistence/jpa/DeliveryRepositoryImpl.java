package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

	private final DeliveryJpaRepository deliveryJpaRepository;
	private final DeliveryMapper deliveryMapper;

	@Override
	public boolean existsByOrderId(UUID orderId) {
		return deliveryJpaRepository.existsByOrderId(orderId);
	}

	@Override
	public Delivery save(Delivery delivery) {
		DeliveryJpaEntity jpaEntity = deliveryMapper.toJpaEntity(delivery);
		DeliveryJpaEntity savedEntity = deliveryJpaRepository.save(jpaEntity);
		return deliveryMapper.toDomain(savedEntity);
	}
}
