package com.firstlogistics.aiservice.application.service;

import com.firstlogistics.aiservice.application.dto.query.SearchAILogsQuery;
import com.firstlogistics.aiservice.application.dto.result.AILogDetailResult;
import com.firstlogistics.aiservice.application.dto.result.AILogSummaryResult;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.repository.AILogQueryRepository;
import com.firstlogistics.aiservice.domain.projection.AILogSearchProjection;
import com.firstlogistics.aiservice.domain.projection.AILogSummaryProjection;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
        given(aiLogQueryRepository.findById(AILogId.of(aiLogId))).willReturn(Optional.of(projection));

        // when
        AILogDetailResult result = aiLogQueryService.getAILog(aiLogId);

        // then
        assertThat(result.id()).isEqualTo(AILogId.of(aiLogId));
        assertThat(result.messageId()).isEqualTo(MessengerMessageId.of(messageId));
        assertThat(result.status()).isEqualTo(AILogStatus.SUCCESS);
        assertThat(result.requestContent()).isEqualTo("테스트 요청");
        assertThat(result.messengerType()).isEqualTo(MessengerType.SLACK);
        assertThat(result.responseContent()).isEqualTo("테스트 응답");
        assertThat(result.systemPrompt()).isEqualTo("시스템 프롬프트");
        assertThat(result.createdAt()).isEqualTo(projection.createdAt());
        verify(aiLogQueryRepository).findById(AILogId.of(aiLogId));
    }

    @Test
    @DisplayName("AI 로그가 존재하지 않으면 예외가 발생한다")
    void getAILog_NotFound() {
        // given
        UUID aiLogId = UUID.randomUUID();

        given(aiLogQueryRepository.findById(AILogId.of(aiLogId))).willReturn(Optional.empty());

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

    @Test
    @DisplayName("검색 조건과 페이징 정보를 이용해 AI 로그 목록을 조회한다")
    void searchAILogs_Success() {
        // 1. Given: 테스트 데이터 및 Mock 설정
        SearchAILogsQuery query = new SearchAILogsQuery(
                UUID.randomUUID(),
                "SUCCESS",
                "SLACK",                      // messengerType
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // Repository가 반환할 Mock 데이터(SummaryDto) 생성
        AILogSummaryProjection summaryDto = new AILogSummaryProjection(
                AILogId.of(UUID.randomUUID()),
                MessengerMessageId.of(UUID.randomUUID()),
                AILogStatus.SUCCESS,
                MessengerType.SLACK,
                "응답 내용 요약"
        );

        Page<AILogSummaryProjection> mockPage = new PageImpl<>(List.of(summaryDto), pageable, 1);

        // Repository 행위 정의
        given(aiLogQueryRepository.searchByCondition(any(AILogSearchProjection.class), any(Pageable.class)))
                .willReturn(mockPage);

        // 2. When: 서비스 메서드 실행
        Page<AILogSummaryResult> resultPage = aiLogQueryService.searchAILogs(query, pageable);

        // 3. Then: 검증
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);

        AILogSummaryResult result = resultPage.getContent().get(0);
        assertThat(result.status()).isEqualTo(AILogStatus.SUCCESS);
        assertThat(result.id()).isEqualTo(summaryDto.aiLogId().id()); // UUID 비교

        // 호출 횟수 검증
        verify(aiLogQueryRepository, times(1)).searchByCondition(any(), any());
    }
}