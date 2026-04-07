package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.command.ChangeProductStatusCommand;
import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.command.UpdateProductCommand;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import common.security.entity.enums.UserRole;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCommandFacade {

    private final CompanyPort companyPort;
    private final ProductCommandService productCommandService;
    private final ProductRepository productRepository;

    public ProductResult register(CreateProductCommand command) {
        CompanyInfo companyInfo = companyPort.getCompany(command.companyId());
        return productCommandService.register(command, companyInfo);
    }

    public ProductResult update(UpdateProductCommand command) {
        CompanyInfo companyInfo = resolveCompanyInfoIfNeeded(command.requesterRole(), command.productId());
        return productCommandService.update(command, companyInfo);
    }

    public ProductResult changeStatus(ChangeProductStatusCommand command) {
        CompanyInfo companyInfo = resolveCompanyInfoIfNeeded(command.requesterRole(), command.productId());
        return productCommandService.changeStatus(command, companyInfo);
    }

    public void delete(UUID productId, UUID requesterId, String requesterRole) {
        CompanyInfo companyInfo = resolveCompanyInfoIfNeeded(requesterRole, productId);
        productCommandService.delete(productId, requesterId, requesterRole, companyInfo);
    }

    private CompanyInfo resolveCompanyInfoIfNeeded(String requesterRole, UUID productId) {
        if (!UserRole.COMPANY_MANAGER.name().equals(requesterRole)) {
            return null;
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return companyPort.getCompany(product.getCompanyId());
    }
}
