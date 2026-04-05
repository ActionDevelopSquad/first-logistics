package com.firstlogistics.deliverservice.infrastructure.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

	private final ExternalServiceErrorCode errorCode;

	public ExternalServiceException(ExternalServiceErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
