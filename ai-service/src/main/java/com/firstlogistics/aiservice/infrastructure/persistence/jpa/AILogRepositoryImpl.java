package com.firstlogistics.aiservice.infrastructure.persistence.jpa;

import com.firstlogistics.aiservice.domain.entity.AILog;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
import com.firstlogistics.aiservice.domain.repository.AILogRepository;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AILogRepositoryImpl implements AILogRepository {
    private final AILogJpaRepository aiLogJpaRepository;
    private final AILogMapper aiLogMapper;

    @Override
    public AILog save(AILog aiLog) {
        try {
            AILogJpaEntity savedEntity = aiLogJpaRepository.save(aiLogMapper.toEntity(aiLog));
            return aiLogMapper.toDomain(savedEntity);
        } catch (DataAccessException e) {
            log.error("AI 로그 저장 중 데이터베이스 에러 발생: {}", e.getMessage());

            throw new AILogException(AILogErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<AILog> findById(AILogId id) {
        return aiLogJpaRepository.findById(id.id())
                .filter(entity -> entity.getDeletedAt() == null) // 삭제된 로그는 무시
                .map(AILogMapper::toDomain);
    }

    @Override
    public void delete(AILog domainAILog, UUID userId) {
        // JPA 엔티티 조회
        AILogJpaEntity entity = aiLogJpaRepository.findById(domainAILog.getId().id())
                .orElseThrow(() -> new AILogException(AILogErrorCode.AILOG_NOT_FOUND));

        // BaseAuditEntity의 softDelete 메서드 호출
        entity.softDelete(userId);
    }
}
