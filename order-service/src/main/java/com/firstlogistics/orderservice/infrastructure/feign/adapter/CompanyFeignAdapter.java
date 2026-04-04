package com.firstlogistics.orderservice.infrastructure.feign.adapter;

import com.firstlogistics.orderservice.application.port.CompanyPort;
import com.firstlogistics.orderservice.application.port.dto.CompanyResponse;
import com.firstlogistics.orderservice.domain.exception.OrderErrorCode;
import com.firstlogistics.orderservice.domain.exception.OrderException;
import com.firstlogistics.orderservice.infrastructure.feign.CompanyClient;
import common.response.ApiResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyFeignAdapter implements CompanyPort {

    private final CompanyClient companyClient;

    @Override
    public CompanyResponse getCompanyById(UUID companyId) {
        try {
            ApiResponse<CompanyResponse> response = companyClient.getCompanyById(companyId);

            if (response != null && response.getStatus().is2xxSuccessful() && response.getData() != null) {
                return response.getData();
            }

            throw new OrderException(OrderErrorCode.COMPANY_NOT_FOUND);

        } catch (FeignException.NotFound e) {
            log.warn("업체 관리자 정보를 찾을 수 없습니다. companyId: {}", companyId);
            throw new OrderException(OrderErrorCode.COMPANY_NOT_FOUND);
        } catch (Exception e) {
            log.error("업체 서비스 통신 중 오류 발생: {}", e.getMessage());
            throw new OrderException(OrderErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }
}