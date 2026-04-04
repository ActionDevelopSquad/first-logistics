package com.firstlogistics.aiservice.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.firstlogistics.aiservice.application.dto.result.AILogDetailResult;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.repository.AILogQueryRepository;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AILogQueryServiceTest {

    @Mock
    private AILogQueryRepository aiLogQueryRepository;

    @InjectMocks
    private AILogQueryService aiLogQueryService;

    @Test
    @DisplayName("AI 로그 단건 조회 성공")
    void getAILog_Success() {
        // given
        UUID aiLogId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        AILogDetailProjection projection = createProjection(aiLogId, messageId);
        given(aiLogQueryRepository.findById(aiLogId)).willReturn(Optional.of(projection));

        // when
        AILogDetailResult result = aiLogQueryService.getAILog(aiLogId);

        // then
        assertThat(result.id()).isEqualTo(AILogId.of(aiLogId));
        assertThat(result.messageId()).isEqualTo(MessengerMessageId.of(messageId));
        assertThat(result.status()).isEqualTo(AILogStatus.SUCCESS);
        assertThat(result.requestContent()).isEqualTo("테스트 요청");
        verify(aiLogQueryRepository).findById(aiLogId);
    }

    @Test
    @DisplayName("AI 로그가 존재하지 않으면 예외가 발생한다")
    void getAILog_NotFound() {
        // given
        UUID aiLogId = UUID.randomUUID();

        given(aiLogQueryRepository.findById(aiLogId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> aiLogQueryService.getAILog(aiLogId))
                .isInstanceOf(AILogException.class)
                .hasMessageContaining(AILogErrorCode.AILOG_NOT_FOUND.getMessage());
    }

    private AILogDetailProjection createProjection(UUID id, UUID messageId) {
        return new AILogDetailProjection(
                AILogId.of(id),
                MessengerMessageId.of(messageId),
                MessengerType.SLACK,
                "테스트 요청",
                "테스트 응답",
                "시스템 프롬프트",
                AILogStatus.SUCCESS,
                LocalDateTime.now()
        );
    }
}