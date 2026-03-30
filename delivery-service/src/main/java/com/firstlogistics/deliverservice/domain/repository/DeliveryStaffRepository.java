package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryStaffRepository {

	// StaffType=HUB_DELIVERY, hubId 일치, deliverySequence 오름차순 첫 번째
	// 시간 충돌 없는 담당자 선택 (active 타임테이블 - CREATED/HUB_MOVING - 와 겹치지 않는 담당자)
	Optional<DeliveryStaff> findNextHubStaff(UUID hubId);

	// StaffType=COMPANY_DELIVERY, hubId 일치, deliverySequence 오름차순 첫 번째
	// 시간 충돌 없는 담당자 선택 (active 타임테이블 - CREATED/HUB_MOVING - 와 겹치지 않는 담당자)
	Optional<DeliveryStaff> findNextCompanyStaff(UUID hubId);
}
