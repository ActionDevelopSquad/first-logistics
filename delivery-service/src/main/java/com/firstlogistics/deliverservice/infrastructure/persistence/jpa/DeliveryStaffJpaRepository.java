package com.firstlogistics.deliverservice.infrastructure.persistence.jpa;

import com.firstlogistics.deliverservice.domain.enums.StaffType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeliveryStaffJpaRepository extends JpaRepository<DeliveryStaffJpaEntity, UUID> {

	@Query("SELECT COALESCE(MAX(ds.deliverySequence), 0) FROM DeliveryStaffJpaEntity ds WHERE ds.deletedAt IS NULL")
	int findMaxSequence();

	/**
	 * 시간 충돌 없는 허브/업체 배송담당자 중 순번이 가장 낮은 담당자 조회
	 * 충돌 조건: 기존 타임테이블의 expectedStartAt < newEnd AND expectedEndAt > newStart (AND status IN ACTIVE)
	 * 정렬: 마지막 배정 종료시간 오름차순(타임테이블 없는 담당자 우선) → deliverySequence 오름차순
	 */
	@Query("""
		SELECT ds FROM DeliveryStaffJpaEntity ds
		LEFT JOIN StaffTimetableJpaEntity st1 ON st1.deliveryStaff = ds
		WHERE ds.hubId = :hubId
		  AND ds.staffType = :staffType
		  AND ds.deletedAt IS NULL
		  AND NOT EXISTS (
		      SELECT 1 FROM StaffTimetableJpaEntity st2
		      WHERE st2.deliveryStaff = ds
		        AND st2.status IN ('CREATED', 'HUB_MOVING')
		        AND st2.expectedStartAt < :assignmentEnd
		        AND st2.expectedEndAt > :assignmentStart
		  )
		GROUP BY ds
		ORDER BY MAX(st1.expectedEndAt) ASC NULLS FIRST, ds.deliverySequence ASC
		""")
	List<DeliveryStaffJpaEntity> findNextAvailableStaff(
		@Param("hubId") UUID hubId,
		@Param("staffType") StaffType staffType,
		@Param("assignmentStart") LocalDateTime assignmentStart,
		@Param("assignmentEnd") LocalDateTime assignmentEnd,
		Pageable pageable
	);
}
