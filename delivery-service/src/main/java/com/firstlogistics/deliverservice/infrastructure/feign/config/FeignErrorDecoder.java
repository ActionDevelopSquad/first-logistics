package com.firstlogistics.deliverservice.infrastructure.feign.config;

import com.firstlogistics.deliverservice.infrastructure.exception.InfraErrorCode;
import com.firstlogistics.deliverservice.infrastructure.exception.InfraException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		return switch (response.status()) {
			case 400 -> new InfraException(InfraErrorCode.EXTERNAL_BAD_REQUEST);
			case 401 -> new InfraException(InfraErrorCode.EXTERNAL_UNAUTHORIZED);
			case 403 -> new InfraException(InfraErrorCode.EXTERNAL_FORBIDDEN);
			case 404 -> handleNotFound(methodKey);
			case 409 -> new InfraException(InfraErrorCode.EXTERNAL_CONFLICT);
			case 412 -> new InfraException(InfraErrorCode.EXTERNAL_PRECONDITION_FAILED);
			default -> new InfraException(InfraErrorCode.EXTERNAL_ERROR);
		};
	}

	private InfraException handleNotFound(String methodKey) {
		if (methodKey.startsWith("HubClient#getHubManagerByUserId")) {
			return new InfraException(InfraErrorCode.HUB_MANAGER_NOT_FOUND);
		}
		if (methodKey.startsWith("HubClient#getHubRoute")) {
			return new InfraException(InfraErrorCode.HUB_CONNECTION_NOT_FOUND);
		}
		if (methodKey.startsWith("HubClient#")) {
			return new InfraException(InfraErrorCode.HUB_NOT_FOUND);
		}
		if (methodKey.startsWith("CompanyClient#getCompaniesByUserId")) {
			return new InfraException(InfraErrorCode.COMPANY_MANAGER_NOT_FOUND);
		}
		if (methodKey.startsWith("CompanyClient#")) {
			return new InfraException(InfraErrorCode.COMPANY_NOT_FOUND);
		}
		if (methodKey.startsWith("UserClient#")) {
			return new InfraException(InfraErrorCode.USER_NOT_FOUND);
		}
		return new InfraException(InfraErrorCode.EXTERNAL_ERROR);
	}
}
