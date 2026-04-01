package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryStaffRepositoryImpl implements DeliveryStaffRepository {

	private final DeliveryStaffJpaRepository deliveryStaffJpaRepository;
	private final DeliveryStaffMapper deliveryStaffMapper;

	@Override
	public Optional<DeliveryStaff> findNextHubDeliveryStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd) {
		List<DeliveryStaffJpaEntity> results = deliveryStaffJpaRepository
			.findNextAvailableStaff(hubId, StaffType.HUB_DELIVERY, assignmentStart, assignmentEnd, PageRequest.of(0, 1));
		return results.isEmpty() ? Optional.empty() : Optional.of(deliveryStaffMapper.toDomain(results.get(0)));
	}

	@Override
	public Optional<DeliveryStaff> findNextCompanyDeliveryStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd) {
		List<DeliveryStaffJpaEntity> results = deliveryStaffJpaRepository
			.findNextAvailableStaff(hubId, StaffType.COMPANY_DELIVERY, assignmentStart, assignmentEnd, PageRequest.of(0, 1));
		return results.isEmpty() ? Optional.empty() : Optional.of(deliveryStaffMapper.toDomain(results.get(0)));
	}

	@Override
	public int findNextSequence() {
		return deliveryStaffJpaRepository.findMaxSequence() + 1;
	}

	@Override
	public DeliveryStaff save(DeliveryStaff deliveryStaff) {
		DeliveryStaffJpaEntity jpaEntity = deliveryStaffMapper.toJpaEntity(deliveryStaff);
		DeliveryStaffJpaEntity savedEntity = deliveryStaffJpaRepository.save(jpaEntity);
		return deliveryStaffMapper.toDomain(savedEntity);
	}
}
