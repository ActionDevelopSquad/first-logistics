package com.firstlogistics.productservice.infrastructure.client;

import com.firstlogistics.productservice.infrastructure.client.dto.CompanyClientResponse;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyPortAdapter implements CompanyPort {

    private final CompanyFeignClient companyFeignClient;

    @Override
    public CompanyInfo getCompany(UUID companyId) {
        try {
            var response = companyFeignClient.getCompany(companyId);
            if (response == null || response.data() == null) {
                throw new ProductException(ProductErrorCode.COMPANY_NOT_FOUND);
            }
            CompanyClientResponse data = response.data();
            return new CompanyInfo(data.id(), data.hubId(), data.managerId());
        } catch (FeignException.NotFound e) {
            throw new ProductException(ProductErrorCode.COMPANY_NOT_FOUND);
        } catch (FeignException e) {
            throw new ProductException(ProductErrorCode.COMPANY_SERVICE_UNAVAILABLE);
        }
    }
}
