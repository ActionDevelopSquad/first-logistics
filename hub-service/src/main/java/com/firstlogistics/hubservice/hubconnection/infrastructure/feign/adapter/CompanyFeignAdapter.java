package com.firstlogistics.hubservice.hubconnection.infrastructure.feign.adapter;

import com.firstlogistics.hubservice.hubconnection.application.port.CompanyPort;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.CompanyResponse;
import com.firstlogistics.hubservice.hubconnection.domain.vo.CompanyId;
import com.firstlogistics.hubservice.hubconnection.infrastructure.feign.CompanyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CompanyFeignAdapter implements CompanyPort {

    private final CompanyClient companyClient;

    @Override
    public CompanyResponse getCompany(CompanyId companyId) {
        return companyClient.getCompany(companyId.id()).data();
    }
}
