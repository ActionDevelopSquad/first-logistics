package com.firstlogistics.notificationservice.ailog.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.ailog.domain.entity.AILog;
import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.ailog.domain.vo.AILogId;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_ai_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class AILogJpaEntity extends BaseAuditEntity {

    @Id
    private UUID id;

    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "request_content", columnDefinition = "TEXT")
    private String requestContent;

    @Column(name = "response_content", columnDefinition = "TEXT")
    private String responseContent;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AILogStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessengerType messengerType;

    public static AILogJpaEntity create(UUID messageId, String request, String response, String system, AILogStatus status, MessengerType type) {
        return new AILogJpaEntity(
                AILogId.of(UUID.randomUUID()).id(),
                messageId,
                request,
                response,
                system,
                status,
                type
        );
    }
}