package com.firstlogistics.notificationservice.ailog.domain.enums;

public enum AILogStatus {
    PENDING,
    SUCCESS,
    RETRY,
    FAILED;

    public boolean canTransitionTo(AILogStatus nextStatus) {
        // 성공(SUCCESS) 상태는 어떤 상태로도 변경 불가
        if (this == SUCCESS) {
            return false;
        }

        return switch (this) {
            case PENDING -> true; // 대기 중엔 모든 상태로 변경 가능
            case RETRY -> (nextStatus == SUCCESS || nextStatus == FAILED);
            case FAILED -> (nextStatus == RETRY || nextStatus == SUCCESS);
            default -> false;
        };
    }
}
