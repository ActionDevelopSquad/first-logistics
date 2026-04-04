package com.firstlogistics.aiservice.domain.entity;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AILog {
    private final AILogId id;
    private MessengerMessageId messageId;
    private String requestContent;
    private String responseContent;
    private String systemPrompt;
    private AILogStatus status;
    private MessengerType messengerType;

    public static AILog create(
            String requestContent,
            String responseContent,
            String systemPrompt,
            AILogStatus status,
            MessengerType messengerType
    ) {
        validateRequired(requestContent, "Request content");
        validateRequired(responseContent, "Response content");
        Objects.requireNonNull(status, "Status is required");
        Objects.requireNonNull(messengerType, "Messenger type is required");
        return new AILog(
                AILogId.of(),
                MessengerMessageId.of(),
                requestContent,
                responseContent,
                systemPrompt,
                status,
                messengerType
        );
    }

    public static AILog reconstitute(
            AILogId id,
            MessengerMessageId messageId,
            String requestContent,
            String responseContent,
            String systemPrompt,
            AILogStatus status,
            MessengerType messengerType
    ) {
        return new AILog(id, messageId, requestContent, responseContent, systemPrompt, status, messengerType);
    }

    public void updateStatus(AILogStatus status) {
        Objects.requireNonNull(status, "Target status cannot be null");

        if (!this.status.canTransitionTo(status)) {
            throw new AILogException(AILogErrorCode.CANNOT_UPDATE_STATUS);
        }

        if (status == AILogStatus.SUCCESS) {
            throw new AILogException(AILogErrorCode.MESSAGE_NOT_EXIST);
        }

        this.status = status;
    }

    private static void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new AILogException(AILogErrorCode.INVALID_PARAMETER);
        }
    }
}
