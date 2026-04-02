package com.firstlogistics.orderservice.presentation.advice;

import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import common.exception.GlobalExceptionHandler;
import common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OrderRestControllerAdvice extends GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        // 어떤 엔티티에서 충돌이 났는지 로그 기록
        log.warn("[OptimisticLockConflict] 엔티티: {}, 메시지: {}",
                e.getPersistentClassName(), e.getMessage());

        // 409 Conflict 응답 반환
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(OrderErrorCode.ALREADY_PROCESSING));
    }
}

