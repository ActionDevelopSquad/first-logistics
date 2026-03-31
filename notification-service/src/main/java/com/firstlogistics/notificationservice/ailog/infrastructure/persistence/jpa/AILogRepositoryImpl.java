package com.firstlogistics.notificationservice.ailog.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.ailog.domain.repository.AILogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AILogRepositoryImpl implements AILogRepository {
    private final AILogJpaRepository aiLogJpaRepository;

}
