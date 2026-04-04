package com.firstlogistics.aiservice.domain.repository;

import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.vo.AILogId;

import java.util.Optional;
import java.util.UUID;

public interface AILogQueryRepository {

    Optional<AILogDetailProjection> findById(UUID aiLogId);
}