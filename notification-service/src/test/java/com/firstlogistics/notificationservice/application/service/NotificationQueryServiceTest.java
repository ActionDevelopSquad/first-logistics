package com.firstlogistics.notificationservice.application.service;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.projection.NotificationSummaryProjection;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Test
    @DisplayName("알림 목록 검색 조회 테스트 - Repository DTO가 Result DTO로 정상 변환되어야 한다")
    void searchNotifications_Success() {
        // given (준비)
        UUID receiverId = UUID.randomUUID();
        NotificationSearchQuery query = new NotificationSearchQuery(receiverId, NotificationType.DEADLINE, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        NotificationSummaryProjection mockDto = new NotificationSummaryProjection(
                UUID.randomUUID(),
                receiverId,
                "발송 안내",
                NotificationType.DEADLINE,
                NotificationStatus.PENDING,
                LocalDateTime.now()
        );

        Page<NotificationSummaryProjection> mockPage = new PageImpl<>(List.of(mockDto), pageable, 1);

        // Repository 동작 정의 (SummaryDto 반환)
        given(notificationRepository.searchByCondition(any(NotificationSearchQuery.class), any(Pageable.class)))
                .willReturn(mockPage);

        // when (실행)
        Page<NotificationSummaryResult> result = notificationQueryService.searchNotifications(query, pageable);

        // then (검증)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        NotificationSummaryResult actual = result.getContent().get(0);
        assertThat(actual.id()).isEqualTo(mockDto.id());
        assertThat(actual.receiverId()).isEqualTo(mockDto.receiverId());
        assertThat(actual.content()).isEqualTo(mockDto.content());
        assertThat(actual.type()).isEqualTo(mockDto.type());
        assertThat(actual.status()).isEqualTo(mockDto.status());

        // 메서드 호출 여부 확인
        verify(notificationRepository).searchByCondition(query, pageable);
    }
}