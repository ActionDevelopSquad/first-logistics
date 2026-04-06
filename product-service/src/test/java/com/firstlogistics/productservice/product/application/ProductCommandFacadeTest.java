package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductCommandFacadeTest {

    @Mock
    private CompanyPort companyPort;

    @Mock
    private ProductCommandService productCommandService;

    @InjectMocks
    private ProductCommandFacade productCommandFacade;

    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private static final CompanyInfo COMPANY_INFO = new CompanyInfo(COMPANY_ID, HUB_ID, MANAGER_ID);

    @Nested
    @DisplayName("상품 등록 (register)")
    class Register {

        private CreateProductCommand command;

        @BeforeEach
        void setUp() {
            command = new CreateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), COMPANY_ID,
                    "마른오징어", BigDecimal.valueOf(15000), 100
            );
        }

        @Test
        @DisplayName("업체 조회 후 서비스에 위임하면 상품 정보를 반환한다")
        void register_success() {
            // given
            ProductResult expected = new ProductResult(
                    UUID.randomUUID(), COMPANY_ID, HUB_ID, "마른오징어",
                    BigDecimal.valueOf(15000), ProductStatus.SELLING.name()
            );
            given(companyPort.getCompany(COMPANY_ID)).willReturn(COMPANY_INFO);
            given(productCommandService.register(any(), any())).willReturn(expected);

            // when
            ProductResult result = productCommandFacade.register(command);

            // then
            assertThat(result).isEqualTo(expected);
            verify(companyPort).getCompany(COMPANY_ID);
            verify(productCommandService).register(command, COMPANY_INFO);
        }

        @Test
        @DisplayName("업체가 존재하지 않으면 예외가 발생한다")
        void register_companyNotFound_throwsException() {
            // given
            willThrow(new ProductException(ProductErrorCode.COMPANY_NOT_FOUND))
                    .given(companyPort).getCompany(COMPANY_ID);

            // when & then
            assertThatThrownBy(() -> productCommandFacade.register(command))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.COMPANY_NOT_FOUND.getMessage());
        }
    }
}
