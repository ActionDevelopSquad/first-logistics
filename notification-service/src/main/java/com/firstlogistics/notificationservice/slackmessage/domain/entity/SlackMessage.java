package com.firstlogistics.notificationservice.slackmessage.domain.entity;

import com.firstlogistics.notificationservice.slackmessage.domain.enums.SlackMessageStatus;
import com.firstlogistics.notificationservice.slackmessage.domain.enums.SlackMessageType;
import com.firstlogistics.notificationservice.slackmessage.domain.exception.SlackMessageErrorCode;
import com.firstlogistics.notificationservice.slackmessage.domain.exception.SlackMessageException;
import com.firstlogistics.notificationservice.slackmessage.domain.vo.SlackMessageId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SlackMessage {
    SlackMessageId id;
    UUID receiverId;
    String slackId;
    String content;
    SlackMessageType type;
    SlackMessageStatus status;
    LocalDateTime readAt;

    public static SlackMessage create(
            UUID receiverId,
            String slackId,
            String content,
            SlackMessageType type
    ) {
        return new SlackMessage(
                SlackMessageId.of(),
                receiverId,
                slackId,
                content,
                type,
                SlackMessageStatus.PENDING,
                null
        );
    }

    public void updateStatus(SlackMessageStatus status) {
        if (!this.status.canTransitionTo(status)) {
                throw new SlackMessageException(SlackMessageErrorCode.CANNOT_UPDATE_STATUS);
        }
        this.status = status;

    }
}
