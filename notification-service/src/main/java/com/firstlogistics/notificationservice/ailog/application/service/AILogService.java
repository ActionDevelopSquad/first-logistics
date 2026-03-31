package com.firstlogistics.notificationservice.ailog.application.service;

import com.firstlogistics.notificationservice.ailog.application.dto.command.CreateAILogCommand;
import com.firstlogistics.notificationservice.ailog.application.dto.result.AILogResult;
import com.firstlogistics.notificationservice.ailog.domain.repository.AILogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AILogService {

    private final AILogRepository aiLogRepository;

    @Transactional
    public AILogResult createAILog(CreateAILogCommand command) {

        return null;
    }
}
