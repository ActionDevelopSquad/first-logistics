package com.firstlogistics.companyservice.presentation;

import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import common.exception.GlobalExceptionHandler;
import common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CompanyExceptionAdvice extends GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("[DataIntegrityViolationException] message: {}", e.getMessage());
        return ResponseEntity
                .status(CompanyErrorCode.DUPLICATE_MANAGER_ID.getStatus())
                .body(ApiResponse.error(CompanyErrorCode.DUPLICATE_MANAGER_ID));
    }
}
