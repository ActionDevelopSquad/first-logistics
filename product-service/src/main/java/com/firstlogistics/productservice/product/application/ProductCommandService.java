package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.command.ChangeProductStatusCommand;
import com.firstlogistics.productservice.product.application.dto.command.UpdateProductCommand;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.event.ProductCreatedEvent;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.vo.Money;
import common.event.Events;
import common.security.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final CompanyPort companyPort;

    @Transactional
    public ProductResult register(CreateProductCommand command, CompanyInfo companyInfo) {
        validateCompanyAccess(command, companyInfo);

        Product product = Product.create(
                companyInfo.companyId(),
                companyInfo.hubId(),
                command.name(),
                new Money(command.price())
        );

        Product saved = productRepository.save(product);

        Events.trigger(new ProductCreatedEvent(saved.getId(), command.stock()));

        return ProductResult.from(saved);
    }

    @Transactional
    public ProductResult update(UpdateProductCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (UserRole.COMPANY_MANAGER.name().equals(command.requesterRole())) {
            CompanyInfo companyInfo = companyPort.getCompany(product.getCompanyId());
            if (!command.requesterId().equals(companyInfo.managerId())) {
                throw new ProductException(ProductErrorCode.UNAUTHORIZED_PRODUCT_UPDATE);
            }
        }

        if (command.name() != null) {
            product.changeName(command.name());
        }
        if (command.price() != null) {
            product.changePrice(new Money(command.price()));
        }

        return ProductResult.from(productRepository.save(product));
    }

    @Transactional
    public ProductResult changeStatus(ChangeProductStatusCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (UserRole.COMPANY_MANAGER.name().equals(command.requesterRole())) {
            CompanyInfo companyInfo = companyPort.getCompany(product.getCompanyId());
            if (!command.requesterId().equals(companyInfo.managerId())) {
                throw new ProductException(ProductErrorCode.UNAUTHORIZED_PRODUCT_STATUS_CHANGE);
            }
        }

        ProductStatus newStatus = resolveProductStatus(command.status());
        if (newStatus == ProductStatus.SELLING) {
            product.startSelling();
        } else {
            product.stopSelling();
        }

        return ProductResult.from(productRepository.save(product));
    }

    private ProductStatus resolveProductStatus(String status) {
        try {
            return ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_STATUS);
        }
    }

    private void validateCompanyAccess(CreateProductCommand command, CompanyInfo companyInfo) {
        if (UserRole.COMPANY_MANAGER.name().equals(command.requesterRole())
                && !command.requesterId().equals(companyInfo.managerId())) {
            throw new ProductException(ProductErrorCode.UNAUTHORIZED_COMPANY_ACCESS);
        }
    }
}
