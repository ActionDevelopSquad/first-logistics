package com.firstlogistics.aiservice.domain.repository;

import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.repository.dto.AILogSearchDto;
import com.firstlogistics.aiservice.domain.repository.dto.AILogSummaryDto;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AILogQueryRepository {

    Optional<AILogDetailProjection> findById(AILogId aiLogId);

    Page<AILogSummaryDto> searchByCondition(AILogSearchDto dto, Pageable pageable);
}