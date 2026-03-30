package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;
import com.firstlogistics.deliverservice.domain.enums.StaffType;
import com.firstlogistics.deliverservice.domain.repository.DeliveryStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryStaffRepositoryImpl implements DeliveryStaffRepository {

	private final DeliveryStaffJpaRepository deliveryStaffJpaRepository;
	private final DeliveryStaffMapper deliveryStaffMapper;

	@Override
	public Optional<DeliveryStaff> findNextHubStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd) {
		return deliveryStaffJpaRepository
			.findNextAvailableStaff(hubId, StaffType.HUB_DELIVERY, assignmentStart, assignmentEnd)
			.map(deliveryStaffMapper::toDomain);
	}

	@Override
	public Optional<DeliveryStaff> findNextCompanyStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd) {
		return deliveryStaffJpaRepository
			.findNextAvailableStaff(hubId, StaffType.COMPANY_DELIVERY, assignmentStart, assignmentEnd)
			.map(deliveryStaffMapper::toDomain);
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
