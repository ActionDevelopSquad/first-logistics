package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryStaffRepository {

	// isDelivering=false, StaffType=HUB_DELIVERY, hubId 일치, deliverySequence 오름차순 첫 번째
	Optional<DeliveryStaff> findNextHubStaff(UUID hubId);

	// isDelivering=false, StaffType=COMPANY_DELIVERY, hubId 일치, deliverySequence 오름차순 첫 번째
	Optional<DeliveryStaff> findNextCompanyStaff(UUID hubId);
}
