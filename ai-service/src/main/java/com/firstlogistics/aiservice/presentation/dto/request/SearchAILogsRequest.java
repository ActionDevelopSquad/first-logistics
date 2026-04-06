package com.firstlogistics.aiservice.presentation.dto.request;

import com.firstlogistics.aiservice.application.dto.query.SearchAILogsQuery;

import java.time.LocalDateTime;
import java.util.UUID;

public record SearchAILogsRequest(
        UUID messageId,
        String status,
        String messengerType,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public SearchAILogsQuery toQuery() {
        return new SearchAILogsQuery(messageId, status, messengerType, startDate, endDate);
    }
}