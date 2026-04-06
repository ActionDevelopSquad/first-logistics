package com.firstlogistics.aiservice.domain.repository.dto;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AILogSearchDto(
        UUID messageId,
        AILogStatus status,
        MessengerType messengerType,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}