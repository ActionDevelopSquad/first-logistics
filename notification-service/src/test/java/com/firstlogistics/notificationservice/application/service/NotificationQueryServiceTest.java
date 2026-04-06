package com.firstlogistics.notificationservice.application.service;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.application.dto.result.NotificationDetailResult;
import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.projection.NotificationDetailProjection;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

    @Test
    @DisplayName("알림 단건 상세 조회 테스트 - 상세 데이터가 Result DTO로 정상 변환되어야 한다")
    void getNotification_Success() {
        // given (준비)
        UUID notificationId = UUID.randomUUID();
        NotificationDetailProjection mockProjection = new NotificationDetailProjection(
                notificationId,
                UUID.randomUUID(),
                "MSG-123",
                "상세 내용입니다.",
                NotificationType.DEADLINE,
                NotificationStatus.SENT,
                MessengerType.SLACK,
                LocalDateTime.now(),
                LocalDateTime.now().minusMinutes(5)
        );

        // Repository가 Optional에 담긴 Projection을 반환하도록 설정
        given(notificationRepository.findDetailById(notificationId))
                .willReturn(Optional.of(mockProjection));

        // when (실행)
        NotificationDetailResult result = notificationQueryService.getNotification(notificationId);

        // then (검증)
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(notificationId);
        assertThat(result.messageId()).isEqualTo("MSG-123");
        assertThat(result.messengerType()).isEqualTo(MessengerType.SLACK);
        assertThat(result.status()).isEqualTo(NotificationStatus.SENT);

        // Repository 호출 확인
        verify(notificationRepository).findDetailById(notificationId);
    }

    @Test
    @DisplayName("알림 단건 조회 실패 테스트 - 존재하지 않는 ID 조회 시 예외가 발생해야 한다")
    void getNotification_NotFound_ThrowsException() {
        // given
        UUID notFoundId = UUID.randomUUID();

        // Repository가 빈 Optional을 반환하도록 설정
        given(notificationRepository.findDetailById(notFoundId))
                .willReturn(Optional.empty());

        // when & then (실행 및 예외 검증)
        assertThatThrownBy(() -> notificationQueryService.getNotification(notFoundId))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining(NotificationErrorCode.NOTIFICATION_NOT_FOUND.getMessage());

        verify(notificationRepository).findDetailById(notFoundId);
    }
}