package com.firstlogistics.notificationservice.domain.entity;

import com.firstlogistics.notificationservice.domain.enums.SlackMessageStatus;
import com.firstlogistics.notificationservice.domain.enums.SlackMessageType;
import com.firstlogistics.notificationservice.domain.exception.SlackMessageErrorCode;
import com.firstlogistics.notificationservice.domain.exception.SlackMessageException;
import com.firstlogistics.notificationservice.domain.vo.SlackMessageId;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="p_slack_message")
public class SlackMessage extends BaseAuditEntity {
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
