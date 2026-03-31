package com.firstlogistics.notificationservice.ailog.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.ailog.domain.entity.AILog;
import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.ailog.domain.vo.AILogId;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "p_ai_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EntityListeners(AuditingEntityListener.class)
public class AILogJpaEntity extends BaseAuditEntity {

    @Id
    private UUID id;

    private UUID messageId;

    @Column(columnDefinition = "TEXT")
    private String requestContent;

    @Column(columnDefinition = "TEXT")
    private String responseContent;

    @Column(columnDefinition = "TEXT")
    private String systemPrompt;

    @Enumerated(EnumType.STRING)
    private AILogStatus status;

    @Enumerated(EnumType.STRING)
    private MessengerType messengerType;
}