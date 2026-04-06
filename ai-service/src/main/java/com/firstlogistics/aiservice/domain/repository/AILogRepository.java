package com.firstlogistics.aiservice.domain.repository;

import com.firstlogistics.aiservice.domain.entity.AILog;
import com.firstlogistics.aiservice.domain.vo.AILogId;

import java.util.Optional;
import java.util.UUID;

public interface AILogRepository {

    AILog save(AILog entity);

    Optional<AILog> findById(AILogId of);

    void delete(AILog aiLog, UUID userId);
}
