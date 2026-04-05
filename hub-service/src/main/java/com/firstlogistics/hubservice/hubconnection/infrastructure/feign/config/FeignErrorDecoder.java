package com.firstlogistics.hubservice.hubconnection.infrastructure.feign.config;


import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    private static final String COMPANY_CLIENT_PREFIX = "CompanyClient#";
    private static final String NAVER_MAP_CLIENT_PREFIX = "NaverMapClient#";

    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("Feign call failed. methodKey={}, status={}", methodKey, response.status());

        if (response.status() == 404) {
            if (methodKey.startsWith(COMPANY_CLIENT_PREFIX)) {
                return new HubConnectionException(HubConnectionErrorCode.COMPANY_NOT_FOUND);
            }
            if (methodKey.startsWith(NAVER_MAP_CLIENT_PREFIX)) {
                return new HubConnectionException(HubConnectionErrorCode.ROUTE_METRIC_UNAVAILABLE);
            }
        }
        return new HubConnectionException(HubConnectionErrorCode.EXTERNAL_SERVICE_ERROR);
    }
}
