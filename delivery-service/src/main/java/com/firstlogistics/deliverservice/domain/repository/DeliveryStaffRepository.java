package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryStaffRepository {

	Optional<DeliveryStaff> findNextHubDeliveryStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	Optional<DeliveryStaff> findNextCompanyDeliveryStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	int findNextSequence();

	DeliveryStaff save(DeliveryStaff deliveryStaff);
}
