package com.firstlogistics.deliverservice.infrastructure.feign.config;

import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		if (response.status() == 404) {
			if (methodKey.startsWith("HubClient#")) {
				return new DeliveryException(DeliveryErrorCode.HUB_NOT_FOUND);
			}
			if (methodKey.startsWith("CompanyClient#")) {
				return new DeliveryException(DeliveryErrorCode.COMPANY_NOT_FOUND);
			}
			if (methodKey.startsWith("UserClient#")) {
				return new DeliveryException(DeliveryErrorCode.USER_NOT_FOUND);
			}
		}
		return new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_ERROR);
	}
}
