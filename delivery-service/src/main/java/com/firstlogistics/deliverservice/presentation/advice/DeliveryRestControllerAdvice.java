package com.firstlogistics.deliverservice.presentation.advice;

import common.exception.GlobalExceptionHandler;
import common.response.ApiResponse;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.infrastructure.exception.ExternalServiceErrorCode;
import com.firstlogistics.deliverservice.infrastructure.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DeliveryRestControllerAdvice extends GlobalExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
		log.warn("[DataIntegrityViolationException] message: {}", e.getMessage());
		return ResponseEntity
			.status(DeliveryErrorCode.DUPLICATE_RESOURCE.getStatus())
			.body(ApiResponse.error(DeliveryErrorCode.DUPLICATE_RESOURCE));
	}

	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<ApiResponse<Void>> handleOptimisticLockingFailure(OptimisticLockingFailureException e) {
		log.warn("[OptimisticLockingFailureException] message: {}", e.getMessage());
		return ResponseEntity
			.status(DeliveryErrorCode.OPTIMISTIC_LOCK_CONFLICT.getStatus())
			.body(ApiResponse.error(DeliveryErrorCode.OPTIMISTIC_LOCK_CONFLICT));
	}

	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(ExternalServiceException e) {
		ExternalServiceErrorCode errorCode = e.getErrorCode();
		log.warn("[ExternalServiceException] code: {}, message: {}", errorCode.getCode(), errorCode.getMessage());
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ApiResponse.error(errorCode));
	}
}
