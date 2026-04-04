package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.vo.Money;
import common.security.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CompanyPort companyPort;

    @Transactional
    public ProductResult register(CreateProductCommand command) {
        CompanyInfo companyInfo = companyPort.getCompany(command.companyId());

        validateCompanyAccess(command, companyInfo);

        Product product = Product.create(
                companyInfo.companyId(),
                companyInfo.hubId(),
                command.name(),
                new Money(command.price())
        );

        Product saved = productRepository.save(product);

        Inventory inventory = Inventory.create(saved.getId(), command.stock(), 0);
        inventoryRepository.save(inventory);

        return ProductResult.from(saved);
    }

    private void validateCompanyAccess(CreateProductCommand command, CompanyInfo companyInfo) {
        if (UserRole.COMPANY_MANAGER.name().equals(command.requesterRole())
                && !command.requesterId().equals(companyInfo.managerId())) {
            throw new ProductException(ProductErrorCode.UNAUTHORIZED_COMPANY_ACCESS);
        }
    }
}
