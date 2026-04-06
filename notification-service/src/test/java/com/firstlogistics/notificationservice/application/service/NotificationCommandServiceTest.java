package com.firstlogistics.notificationservice.application.service;

import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.enums.NotificationStatus;
import com.firstlogistics.notificationservice.domain.enums.NotificationType;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import com.firstlogistics.notificationservice.domain.vo.NotificationId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationCommandService notificationCommandService;

    @Test
    @DisplayName("알림 삭제 성공 - 존재하는 알림인 경우 repository.delete를 호출한다")
    void deleteNotification_Success() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Notification mockNotification = Notification.reconstitute(
                NotificationId.of(notificationId),
                1L,
                receiverId,
                "MSG-1004",
                "테스트 알림 내용입니다.",
                NotificationType.DEADLINE,
                NotificationStatus.PENDING,
                MessengerType.SLACK,
                null
        );

        given(notificationRepository.findById(any(NotificationId.class)))
                .willReturn(Optional.of(mockNotification));

        // when
        notificationCommandService.deleteNotification(notificationId, userId);

        // then
        verify(notificationRepository).delete(mockNotification, userId);
    }

    @Test
    @DisplayName("알림 삭제 실패 - 존재하지 않는 알림 ID인 경우 예외를 던진다")
    void deleteNotification_NotFound_ThrowsException() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        given(notificationRepository.findById(any(NotificationId.class)))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationCommandService.deleteNotification(notificationId, userId))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining(NotificationErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
    }
}