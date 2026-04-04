package com.firstlogistics.deliverservice.infrastructure.feign.config;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		return switch (response.status()) {
			case 400 -> new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_BAD_REQUEST);
			case 401 -> new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_UNAUTHORIZED);
			case 403 -> new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_FORBIDDEN);
			case 404 -> handleNotFound(methodKey);
			case 409 -> new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_CONFLICT);
			case 412 -> new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_PRECONDITION_FAILED);
			default -> new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_ERROR);
		};
	}

	private DeliveryException handleNotFound(String methodKey) {
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
		return new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_ERROR);
	}
}
