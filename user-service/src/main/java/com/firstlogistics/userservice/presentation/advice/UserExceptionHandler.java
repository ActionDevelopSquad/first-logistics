package com.firstlogistics.userservice.presentation.advice;

import common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler extends GlobalExceptionHandler {

}
