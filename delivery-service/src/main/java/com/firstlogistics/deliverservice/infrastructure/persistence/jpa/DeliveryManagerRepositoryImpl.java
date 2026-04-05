package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.DeliveryManager;
import com.firstlogistics.deliverservice.domain.enums.ManagerType;
import com.firstlogistics.deliverservice.domain.enums.TimetableStatus;
import com.firstlogistics.deliverservice.domain.repository.DeliveryManagerRepository;
import com.firstlogistics.deliverservice.domain.vo.DeliveryManagerId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerRepositoryImpl implements DeliveryManagerRepository {

	private final DeliveryManagerJpaRepository deliveryManagerJpaRepository;
	private final DeliveryManagerMapper deliveryManagerMapper;

	@Override
	public Optional<DeliveryManager> findNextHubDeliveryManager(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd) {
		List<TimetableStatus> activeStatuses = List.of(TimetableStatus.CREATED, TimetableStatus.HUB_MOVING);
		List<DeliveryManagerJpaEntity> results = deliveryManagerJpaRepository
			.findNextAvailableManager(hubId, ManagerType.HUB_DELIVERY, activeStatuses, assignmentStart, assignmentEnd, PageRequest.of(0, 1));
		return results.isEmpty() ? Optional.empty() : Optional.of(deliveryManagerMapper.toDomain(results.get(0)));
	}

	@Override
	public Optional<DeliveryManager> findNextCompanyDeliveryManager(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd) {
		List<TimetableStatus> activeStatuses = List.of(TimetableStatus.CREATED, TimetableStatus.HUB_MOVING);
		List<DeliveryManagerJpaEntity> results = deliveryManagerJpaRepository
			.findNextAvailableManager(hubId, ManagerType.COMPANY_DELIVERY, activeStatuses, assignmentStart, assignmentEnd, PageRequest.of(0, 1));
		return results.isEmpty() ? Optional.empty() : Optional.of(deliveryManagerMapper.toDomain(results.get(0)));
	}

	@Override
	public Optional<DeliveryManager> findById(DeliveryManagerId id) {
		return deliveryManagerJpaRepository.findById(id.id())
			.filter(entity -> entity.getDeletedAt() == null)
			.map(deliveryManagerMapper::toDomain);
	}

	@Override
	public boolean existsByUserId(UUID userId) {
		return deliveryManagerJpaRepository.existsByUserIdAndDeletedAtIsNull(userId);
	}

	@Override
	public Optional<DeliveryManager> findByUserId(UUID userId) {
		return deliveryManagerJpaRepository.findByUserIdAndDeletedAtIsNull(userId)
			.map(deliveryManagerMapper::toDomain);
	}

	@Override
	public int findNextSequence() {
		return deliveryManagerJpaRepository.findMaxSequence() + 1;
	}

	@Override
	public DeliveryManager save(DeliveryManager deliveryManager) {
		DeliveryManagerJpaEntity jpaEntity = deliveryManagerMapper.toJpaEntity(deliveryManager);
		DeliveryManagerJpaEntity savedEntity = deliveryManagerJpaRepository.save(jpaEntity);
		return deliveryManagerMapper.toDomain(savedEntity);
	}
}
