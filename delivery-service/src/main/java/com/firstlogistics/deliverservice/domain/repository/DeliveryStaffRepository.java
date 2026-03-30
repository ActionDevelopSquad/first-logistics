package com.firstlogistics.deliverservice.domain.repository;

import com.firstlogistics.deliverservice.domain.entity.DeliveryStaff;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryStaffRepository {

	// StaffType=HUB_DELIVERY, hubId 일치, 시간 충돌 없는 담당자 중 마지막 배정 종료시간 오름차순 (타임테이블 없는 담당자 우선)
	Optional<DeliveryStaff> findNextHubStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	// StaffType=COMPANY_DELIVERY, hubId 일치, 시간 충돌 없는 담당자 중 마지막 배정 종료시간 오름차순 (타임테이블 없는 담당자 우선)
	Optional<DeliveryStaff> findNextCompanyStaff(UUID hubId, LocalDateTime assignmentStart, LocalDateTime assignmentEnd);

	int findNextSequence();

	DeliveryStaff save(DeliveryStaff deliveryStaff);
}
