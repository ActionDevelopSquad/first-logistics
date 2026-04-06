package com.firstlogistics.notificationservice.domain.enums;

public enum NotificationStatus {
    PENDING,
    SENT,
    RETRY,
    FAILED;

    public boolean canTransitionTo(NotificationStatus nextStatus) {
        // 성공(SENT) 상태는 어떤 상태로도 변경 불가
        if (this == SENT) {
            return false;
        }

        return switch (this) {
            case PENDING -> true; // 대기 중엔 모든 상태로 변경 가능
            case RETRY -> (nextStatus == SENT || nextStatus == FAILED);
            case FAILED -> (nextStatus == RETRY || nextStatus == SENT);
            default -> false;
        };
    }
}
