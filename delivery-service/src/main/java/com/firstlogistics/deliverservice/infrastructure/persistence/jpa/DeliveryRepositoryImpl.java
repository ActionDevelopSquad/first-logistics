package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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
	public Optional<Delivery> findById(DeliveryId deliveryId) {
		return deliveryJpaRepository.findById(deliveryId.id())
			.map(deliveryMapper::toDomain);
	}

	@Override
	public Optional<Delivery> findByOrderId(UUID orderId) {
		return deliveryJpaRepository.findByOrderId(orderId)
			.map(deliveryMapper::toDomain);
	}

	@Override
	public Delivery save(Delivery delivery) {
		DeliveryJpaEntity jpaEntity = deliveryMapper.toJpaEntity(delivery);
		DeliveryJpaEntity savedEntity = deliveryJpaRepository.save(jpaEntity);
		return deliveryMapper.toDomain(savedEntity);
	}
}
