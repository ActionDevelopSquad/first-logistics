package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.command.ChangeProductStatusCommand;
import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.command.UpdateProductCommand;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.vo.Money;
import common.security.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductCommandFacadeTest {

    @Mock
    private CompanyPort companyPort;

    @Mock
    private ProductCommandService productCommandService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductCommandFacade productCommandFacade;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private CompanyInfo companyInfo;

    @BeforeEach
    void setUp() {
        companyInfo = new CompanyInfo(COMPANY_ID, HUB_ID, MANAGER_ID);
    }

    @Nested
    @DisplayName("상품 등록 (register)")
    class Register {

        @Test
        @DisplayName("register 호출 시 companyPort에서 업체 정보를 조회 후 서비스에 전달한다")
        void register_fetchesCompanyThenDelegates() {
            // given
            CreateProductCommand command = new CreateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), COMPANY_ID,
                    "마른오징어", BigDecimal.valueOf(15000), 100
            );
            given(companyPort.getCompany(COMPANY_ID)).willReturn(companyInfo);
            given(productCommandService.register(any(), any())).willReturn(null);

            // when
            productCommandFacade.register(command);

            // then
            verify(companyPort).getCompany(COMPANY_ID);
            verify(productCommandService).register(eq(command), eq(companyInfo));
        }
    }

    @Nested
    @DisplayName("상품 수정 (update)")
    class Update {

        @Test
        @DisplayName("MASTER 권한이면 companyPort를 호출하지 않고 서비스에 null companyInfo를 전달한다")
        void update_master_noCompanyFetch() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), PRODUCT_ID,
                    "건오징어", BigDecimal.valueOf(20000)
            );
            given(productCommandService.update(any(), any())).willReturn(null);

            // when
            productCommandFacade.update(command);

            // then
            verify(productCommandService).update(eq(command), eq(null));
        }

        @Test
        @DisplayName("COMPANY_MANAGER 권한이면 상품 조회 후 업체 정보를 서비스에 전달한다")
        void update_companyManager_fetchesCompanyInfo() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.COMPANY_MANAGER.name(), PRODUCT_ID,
                    "건오징어", null
            );
            Product product = Product.reconstitute(
                    PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                    Money.krw(15000), ProductStatus.SELLING
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
            given(companyPort.getCompany(COMPANY_ID)).willReturn(companyInfo);
            given(productCommandService.update(any(), any())).willReturn(null);

            // when
            productCommandFacade.update(command);

            // then
            verify(companyPort).getCompany(COMPANY_ID);
            verify(productCommandService).update(eq(command), eq(companyInfo));
        }

        @Test
        @DisplayName("COMPANY_MANAGER 권한인데 상품이 없으면 예외가 발생한다")
        void update_companyManager_productNotFound_throwsException() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.COMPANY_MANAGER.name(), PRODUCT_ID,
                    "건오징어", null
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productCommandFacade.update(command))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 삭제 (delete)")
    class Delete {

        @Test
        @DisplayName("MASTER 권한이면 companyPort를 호출하지 않고 서비스에 위임한다")
        void delete_master_noCompanyFetch() {
            // when
            productCommandFacade.delete(PRODUCT_ID, MANAGER_ID, UserRole.MASTER.name());

            // then
            verify(productCommandService).delete(eq(PRODUCT_ID), eq(MANAGER_ID), eq(UserRole.MASTER.name()), eq(null));
        }

        @Test
        @DisplayName("COMPANY_MANAGER 권한이면 상품 조회 후 업체 정보를 서비스에 전달한다")
        void delete_companyManager_fetchesCompanyInfo() {
            // given
            Product product = Product.reconstitute(
                    PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                    Money.krw(15000), ProductStatus.SELLING
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
            given(companyPort.getCompany(COMPANY_ID)).willReturn(companyInfo);

            // when
            productCommandFacade.delete(PRODUCT_ID, MANAGER_ID, UserRole.COMPANY_MANAGER.name());

            // then
            verify(companyPort).getCompany(COMPANY_ID);
            verify(productCommandService).delete(eq(PRODUCT_ID), eq(MANAGER_ID), eq(UserRole.COMPANY_MANAGER.name()), eq(companyInfo));
        }
    }

    @Nested
    @DisplayName("상품 상태 변경 (changeStatus)")
    class ChangeStatus {

        @Test
        @DisplayName("HUB_MANAGER 권한이면 companyPort를 호출하지 않는다")
        void changeStatus_hubManager_noCompanyFetch() {
            // given
            ChangeProductStatusCommand command = new ChangeProductStatusCommand(
                    MANAGER_ID, UserRole.HUB_MANAGER.name(), PRODUCT_ID, "STOPPED"
            );
            given(productCommandService.changeStatus(any(), any())).willReturn(null);

            // when
            productCommandFacade.changeStatus(command);

            // then
            verify(productCommandService).changeStatus(eq(command), eq(null));
        }

        @Test
        @DisplayName("COMPANY_MANAGER 권한이면 업체 정보를 서비스에 전달한다")
        void changeStatus_companyManager_fetchesCompanyInfo() {
            // given
            ChangeProductStatusCommand command = new ChangeProductStatusCommand(
                    MANAGER_ID, UserRole.COMPANY_MANAGER.name(), PRODUCT_ID, "STOPPED"
            );
            Product product = Product.reconstitute(
                    PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                    Money.krw(15000), ProductStatus.SELLING
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
            given(companyPort.getCompany(COMPANY_ID)).willReturn(companyInfo);
            given(productCommandService.changeStatus(any(), any())).willReturn(null);

            // when
            productCommandFacade.changeStatus(command);

            // then
            verify(companyPort).getCompany(COMPANY_ID);
            verify(productCommandService).changeStatus(eq(command), eq(companyInfo));
        }
    }
}
