package com.firstlogistics.hubservice.hubconnection.infrastructure.feign.config;


import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            if (methodKey.startsWith("CompanyClient#")) {
                return new HubConnectionException(HubConnectionErrorCode.COMPANY_NOT_FOUND);
            }
            if (methodKey.startsWith("NaverMapClient#")) {
                return new HubConnectionException(HubConnectionErrorCode.EXTERNAL_SERVICE_ERROR);
            }
        }
        return new HubConnectionException(HubConnectionErrorCode.EXTERNAL_SERVICE_ERROR);
    }
}
