package com.firstlogistics.deliverservice.infrastructure.feign.config;

import com.firstlogistics.deliverservice.domain.exception.DeliveryCreationException;
import com.firstlogistics.deliverservice.domain.exception.DeliveryErrorCode;
import com.firstlogistics.deliverservice.domain.exception.DeliveryException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		if (response.status() == 404) {
			if (methodKey.startsWith("HubClient#")) {
				return new DeliveryCreationException(DeliveryErrorCode.HUB_NOT_FOUND);
			}
			if (methodKey.startsWith("CompanyClient#")) {
				return new DeliveryCreationException(DeliveryErrorCode.COMPANY_NOT_FOUND);
			}
			if (methodKey.startsWith("UserClient#")) {
				return new DeliveryCreationException(DeliveryErrorCode.USER_NOT_FOUND);
			}
		}
		// 5xx 등 일시 장애: 재시도 후 DLT (자동 Saga 보상 없음)
		return new DeliveryException(DeliveryErrorCode.EXTERNAL_SERVICE_ERROR);
	}
}
