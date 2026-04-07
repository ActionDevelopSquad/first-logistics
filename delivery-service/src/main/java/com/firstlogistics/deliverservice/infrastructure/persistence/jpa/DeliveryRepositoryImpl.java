package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.Delivery;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.domain.repository.DeliveryRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

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
		return deliveryJpaRepository.findByIdWithRoutes(deliveryId.id())
			.map(deliveryMapper::toDomain);
	}

	@Override
	public Optional<Delivery> findByOrderId(UUID orderId) {
		return deliveryJpaRepository.findByOrderId(orderId)
			.map(deliveryMapper::toDomain);
	}

	@Override
	public Delivery save(Delivery delivery) {
		UUID id = delivery.getId().id();
		Optional<DeliveryJpaEntity> existing = deliveryJpaRepository.findByIdWithRoutes(id);

		DeliveryJpaEntity jpaEntity;
		if (existing.isPresent()) {
			jpaEntity = existing.get();
			jpaEntity.update(delivery, deliveryMapper);
		} else {
			jpaEntity = deliveryMapper.toJpaEntity(delivery);
		}

		DeliveryJpaEntity savedEntity = deliveryJpaRepository.save(jpaEntity);
		return deliveryMapper.toDomain(savedEntity);
	}

	@Override
	public void deleteById(DeliveryId deliveryId, UUID userId) {
		DeliveryJpaEntity jpaEntity = deliveryJpaRepository.findById(deliveryId.id())
			.orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
		jpaEntity.softDelete(userId);
		jpaEntity.getRoutes().forEach(route -> route.softDelete(userId));
	}
}
