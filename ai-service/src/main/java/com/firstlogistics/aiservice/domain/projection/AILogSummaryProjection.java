package com.firstlogistics.aiservice.domain.projection;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;
import com.querydsl.core.annotations.QueryProjection;

public record AILogSummaryProjection(
        AILogId aiLogId,
        MessengerMessageId messageId,
        AILogStatus status,
        MessengerType messengerType,
        String responseContent
) {
    @QueryProjection
    public AILogSummaryProjection {
    }
}