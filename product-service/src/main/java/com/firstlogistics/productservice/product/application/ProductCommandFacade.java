package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCommandFacade {

    private final CompanyPort companyPort;
    private final ProductCommandService productCommandService;

    public ProductResult register(CreateProductCommand command) {
        CompanyInfo companyInfo = companyPort.getCompany(command.companyId());
        return productCommandService.register(command, companyInfo);
    }
}
