package com.firstlogistics.aiservice.application.service;

import com.firstlogistics.aiservice.application.dto.result.AILogDetailResult;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.repository.AILogQueryRepository;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AILogQueryService {
    private final AILogQueryRepository aiLogQueryRepository;

    public AILogDetailResult getAILog(UUID aiLogId) {
        AILogDetailProjection projection = aiLogQueryRepository.findById(AILogId.of(aiLogId))
                .orElseThrow(() -> new AILogException(AILogErrorCode.AILOG_NOT_FOUND));

        return AILogDetailResult.from(projection);
    }
}