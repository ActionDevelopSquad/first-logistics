package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.product.application.dto.command.CreateProductCommand;
import com.firstlogistics.productservice.product.application.dto.command.UpdateProductCommand;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.event.ProductCreatedEvent;
import com.firstlogistics.productservice.product.domain.vo.Money;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import common.event.Events;
import common.security.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ProductCommandService productCommandService;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID OTHER_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    private static final CompanyInfo COMPANY_INFO = new CompanyInfo(COMPANY_ID, HUB_ID, MANAGER_ID);

    @BeforeEach
    void initEvents() {
        new Events().init(applicationEventPublisher);
    }

    @Nested
    @DisplayName("상품 등록 (register)")
    class Register {

        private CreateProductCommand masterCommand;

        @BeforeEach
        void setUp() {
            masterCommand = new CreateProductCommand(
                    MANAGER_ID,
                    UserRole.MASTER.name(),
                    COMPANY_ID,
                    "마른오징어",
                    BigDecimal.valueOf(15000),
                    100
            );
        }

        @Test
        @DisplayName("MASTER 권한으로 상품을 등록하면 저장된 상품 정보를 반환한다")
        void register_master_success() {
            // given
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResult result = productCommandService.register(masterCommand, COMPANY_INFO);

            // then
            assertThat(result.companyId()).isEqualTo(COMPANY_ID);
            assertThat(result.hubId()).isEqualTo(HUB_ID);
            assertThat(result.name()).isEqualTo("마른오징어");
            assertThat(result.status()).isEqualTo(ProductStatus.SELLING.name());
        }

        @Test
        @DisplayName("상품 등록 후 ProductCreatedEvent가 발행된다")
        void register_publishesProductCreatedEvent() {
            // given
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            productCommandService.register(masterCommand, COMPANY_INFO);

            // then
            verify(applicationEventPublisher).publishEvent(any(ProductCreatedEvent.class));
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 본인 업체에 상품을 등록하면 성공한다")
        void register_companyManager_ownCompany_success() {
            // given
            CreateProductCommand command = new CreateProductCommand(
                    MANAGER_ID, UserRole.COMPANY_MANAGER.name(), COMPANY_ID,
                    "마른오징어", BigDecimal.valueOf(15000), 100
            );
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResult result = productCommandService.register(command, COMPANY_INFO);

            // then
            assertThat(result.companyId()).isEqualTo(COMPANY_ID);
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 다른 업체에 상품을 등록하면 예외가 발생한다")
        void register_companyManager_otherCompany_throwsException() {
            // given
            CreateProductCommand command = new CreateProductCommand(
                    OTHER_USER_ID, UserRole.COMPANY_MANAGER.name(), COMPANY_ID,
                    "마른오징어", BigDecimal.valueOf(15000), 100
            );

            // when & then
            assertThatThrownBy(() -> productCommandService.register(command, COMPANY_INFO))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.UNAUTHORIZED_COMPANY_ACCESS.getMessage());
        }

        @Test
        @DisplayName("상품 등록 시 Product가 저장된다")
        void register_savesProduct() {
            // given
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            productCommandService.register(masterCommand, COMPANY_INFO);

            // then
            verify(productRepository).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("상품 수정 (update)")
    class Update {

        private Product existingProduct;

        @BeforeEach
        void setUp() {
            existingProduct = Product.reconstitute(
                    PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                    Money.krw(15000), ProductStatus.SELLING
            );
        }

        @Test
        @DisplayName("MASTER 권한으로 이름과 가격을 수정하면 성공한다")
        void update_master_success() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), PRODUCT_ID,
                    "건오징어", BigDecimal.valueOf(20000)
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResult result = productCommandService.update(command);

            // then
            assertThat(result.name()).isEqualTo("건오징어");
            assertThat(result.price()).isEqualByComparingTo(BigDecimal.valueOf(20000));
        }

        @Test
        @DisplayName("이름만 수정하면 가격은 변경되지 않는다")
        void update_nameOnly_priceUnchanged() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), PRODUCT_ID,
                    "건오징어", null
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResult result = productCommandService.update(command);

            // then
            assertThat(result.name()).isEqualTo("건오징어");
            assertThat(result.price()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        }

        @Test
        @DisplayName("가격만 수정하면 이름은 변경되지 않는다")
        void update_priceOnly_nameUnchanged() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), PRODUCT_ID,
                    null, BigDecimal.valueOf(20000)
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResult result = productCommandService.update(command);

            // then
            assertThat(result.name()).isEqualTo("마른오징어");
            assertThat(result.price()).isEqualByComparingTo(BigDecimal.valueOf(20000));
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 본인 업체 상품을 수정하면 성공한다")
        void update_companyManager_ownCompany_success() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.COMPANY_MANAGER.name(), PRODUCT_ID,
                    "건오징어", BigDecimal.valueOf(20000)
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(existingProduct));
            given(companyPort.getCompany(COMPANY_ID))
                    .willReturn(new CompanyInfo(COMPANY_ID, HUB_ID, MANAGER_ID));
            given(productRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            ProductResult result = productCommandService.update(command);

            // then
            assertThat(result.name()).isEqualTo("건오징어");
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 다른 업체 상품을 수정하면 예외가 발생한다")
        void update_companyManager_otherCompany_throwsException() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    OTHER_USER_ID, UserRole.COMPANY_MANAGER.name(), PRODUCT_ID,
                    "건오징어", null
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(existingProduct));
            given(companyPort.getCompany(COMPANY_ID))
                    .willReturn(new CompanyInfo(COMPANY_ID, HUB_ID, MANAGER_ID));

            // when & then
            assertThatThrownBy(() -> productCommandService.update(command))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.UNAUTHORIZED_PRODUCT_UPDATE.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 productId로 수정하면 예외가 발생한다")
        void update_productNotFound_throwsException() {
            // given
            UpdateProductCommand command = new UpdateProductCommand(
                    MANAGER_ID, UserRole.MASTER.name(), PRODUCT_ID,
                    "건오징어", null
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productCommandService.update(command))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }
}
