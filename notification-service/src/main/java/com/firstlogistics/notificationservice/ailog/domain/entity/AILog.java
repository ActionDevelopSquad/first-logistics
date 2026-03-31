package com.firstlogistics.notificationservice.ailog.domain.entity;

import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.ailog.domain.exception.AILogErrorCode;
import com.firstlogistics.notificationservice.ailog.domain.exception.AILogException;
import com.firstlogistics.notificationservice.ailog.domain.vo.AILogId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AILog {
    private final AILogId id;
    private UUID messageId;
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
        return new AILog(
                AILogId.of(),
                null,  // 슬랙 메시지 생성 후 저장
                requestContent,
                responseContent,
                systemPrompt,
                status,
                messengerType
        );
    }

    public static AILog reconstitute(
            AILogId id,
            UUID messageId,
            String requestContent,
            String responseContent,
            String systemPrompt,
            AILogStatus status,
            MessengerType messengerType
    ) {
        return new AILog(id, messageId, requestContent, responseContent, systemPrompt, status, messengerType);
    }

    public void updateSlackMessageId(UUID slackMessageId) {
        // null 체크
        if (slackMessageId == null) {
            throw new AILogException(AILogErrorCode.MESSAGE_NOT_EXIST);
        }

        // 이미 값이 있는데 또 바꾸려고 할 때
        if (this.messageId != null) {
            throw new AILogException(AILogErrorCode.MESSAGE_ALREADY_EXIST);
        }

        this.messageId = slackMessageId;
    }

    public void updateStatus(AILogStatus status) {
        if (!this.status.canTransitionTo(status)) {
            throw new AILogException(AILogErrorCode.CANNOT_UPDATE_STATUS);
        }
        this.status = status;

    }
}
