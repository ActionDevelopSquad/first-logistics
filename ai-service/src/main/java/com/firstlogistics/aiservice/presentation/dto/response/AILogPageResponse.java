package com.firstlogistics.aiservice.presentation.dto.response;

import com.firstlogistics.aiservice.application.dto.result.AILogSummaryResult;
import org.springframework.data.domain.Page;

public record AILogPageResponse(
        Page<AILogSummaryResponse> logs
) {
    public static AILogPageResponse from(Page<AILogSummaryResult> resultPage) {
        return new AILogPageResponse(resultPage.map(AILogSummaryResponse::from));
    }
}