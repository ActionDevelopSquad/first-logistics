package com.firstlogistics.aiservice.domain.repository;

import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.projection.AILogSearchProjection;
import com.firstlogistics.aiservice.domain.projection.AILogSummaryProjection;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AILogQueryRepository {

    Optional<AILogDetailProjection> findById(AILogId aiLogId);

    Page<AILogSummaryProjection> searchByCondition(AILogSearchProjection dto, Pageable pageable);
}