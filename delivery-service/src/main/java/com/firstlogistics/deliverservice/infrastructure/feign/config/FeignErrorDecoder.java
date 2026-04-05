package com.firstlogistics.deliverservice.infrastructure.feign.config;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import com.firstlogistics.deliverservice.infrastructure.exception.ExternalServiceErrorCode;
import com.firstlogistics.deliverservice.infrastructure.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		return switch (response.status()) {
			case 400 -> new ExternalServiceException(ExternalServiceErrorCode.BAD_REQUEST);
			case 401 -> new ExternalServiceException(ExternalServiceErrorCode.UNAUTHORIZED);
			case 403 -> new ExternalServiceException(ExternalServiceErrorCode.FORBIDDEN);
			case 404 -> handleNotFound(methodKey);
			case 409 -> new ExternalServiceException(ExternalServiceErrorCode.CONFLICT);
			case 412 -> new ExternalServiceException(ExternalServiceErrorCode.PRECONDITION_FAILED);
			default -> new ExternalServiceException(ExternalServiceErrorCode.INTERNAL_ERROR);
		};
	}

	private RuntimeException handleNotFound(String methodKey) {
		if (methodKey.startsWith("HubClient#getHubManagerByUserId")) {
			return new DeliveryException(DeliveryErrorCode.HUB_MANAGER_NOT_FOUND);
		}
		if (methodKey.startsWith("HubClient#getHubRoute")) {
			return new DeliveryException(DeliveryErrorCode.HUB_CONNECTION_NOT_FOUND);
		}
		if (methodKey.startsWith("HubClient#")) {
			return new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND);
		}
		if (methodKey.startsWith("CompanyClient#getCompaniesByUserId")) {
			return new DeliveryException(DeliveryErrorCode.COMPANY_MANAGER_NOT_FOUND);
		}
		if (methodKey.startsWith("CompanyClient#")) {
			return new DeliveryException(DeliveryErrorCode.COMPANY_NOT_FOUND);
		}
		if (methodKey.startsWith("UserClient#")) {
			return new DeliveryException(DeliveryErrorCode.USER_NOT_FOUND);
		}
		return new ExternalServiceException(ExternalServiceErrorCode.INTERNAL_ERROR);
	}
}
