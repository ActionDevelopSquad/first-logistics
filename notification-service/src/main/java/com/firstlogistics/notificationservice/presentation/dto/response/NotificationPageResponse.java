package com.firstlogistics.notificationservice.presentation.dto.response;

import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import org.springframework.data.domain.Page;

public record NotificationPageResponse(
        Page<NotificationResponse> notifications
) {
    public static NotificationPageResponse from(Page<NotificationSummaryResult> resultPage) {
        return new NotificationPageResponse(resultPage.map(NotificationResponse::from));
    }
}