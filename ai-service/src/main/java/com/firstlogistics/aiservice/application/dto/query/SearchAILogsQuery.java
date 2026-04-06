package com.firstlogistics.aiservice.application.dto.query;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.projection.AILogSearchProjection;

import java.time.LocalDateTime;
import java.util.UUID;

public record SearchAILogsQuery(
        UUID messageId,
        AILogStatus status,
        MessengerType messengerType,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public SearchAILogsQuery(UUID messageId, String status, String messengerType, LocalDateTime startDate, LocalDateTime endDate) {
        this(messageId, parseStatus(status), parseMessenger(messengerType), startDate, endDate);
    }

    public AILogSearchProjection toDto() {
        return new AILogSearchProjection(messageId, status, messengerType, startDate, endDate);
    }

    private static AILogStatus parseStatus(String status) {
        return (status == null || status.isBlank()) ? null : AILogStatus.valueOf(status.toUpperCase());
    }

    private static MessengerType parseMessenger(String type) {
        return (type == null || type.isBlank()) ? null : MessengerType.valueOf(type.toUpperCase());
    }
}