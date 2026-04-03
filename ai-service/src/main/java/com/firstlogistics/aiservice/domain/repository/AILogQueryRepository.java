package com.firstlogistics.aiservice.domain.repository;

import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;

import java.util.Optional;
import java.util.UUID;

public interface AILogQueryRepository {
    // 단건 조회용 (상세 데이터)
    Optional<AILogDetailProjection> findById(UUID aiLogId);

}