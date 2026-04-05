package com.firstlogistics.productservice.product.application;

import com.firstlogistics.productservice.inventory.application.InventoryQueryService;
import com.firstlogistics.productservice.inventory.application.dto.result.InventoryResult;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryErrorCode;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import com.firstlogistics.productservice.product.application.dto.query.ProductSearchQuery;
import com.firstlogistics.productservice.product.application.dto.result.ProductResult;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryQueryService inventoryQueryService;

    @InjectMocks
    private ProductQueryService productQueryService;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.reconstitute(
                PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                Money.krw(15000), ProductStatus.SELLING
        );
    }

    @Nested
    @DisplayName("목록 조회 (search)")
    class Search {

        @Test
        @DisplayName("조건에 맞는 상품 목록을 페이지로 반환한다")
        void search_returnsPagedResult() {
            // given
            Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
            given(productRepository.findAll(any(), any())).willReturn(page);

            // when
            Page<ProductResult> result = productQueryService.search(
                    new ProductSearchQuery(null, null, null, null), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            ProductResult first = result.getContent().get(0);
            assertThat(first.id()).isEqualTo(PRODUCT_ID);
            assertThat(first.name()).isEqualTo("마른오징어");
        }

        @Test
        @DisplayName("조건에 맞는 상품이 없으면 빈 페이지를 반환한다")
        void search_returnsEmptyPage() {
            // given
            Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            given(productRepository.findAll(any(), any())).willReturn(emptyPage);

            // when
            Page<ProductResult> result = productQueryService.search(
                    new ProductSearchQuery("없는상품", null, null, null), PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("유효하지 않은 status 값이면 예외가 발생한다")
        void search_invalidStatus_throwsException() {
            assertThatThrownBy(() -> productQueryService.search(
                    new ProductSearchQuery(null, null, null, "INVALID"), PageRequest.of(0, 10)))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.INVALID_PRODUCT_STATUS.getMessage());
        }
    }

    @Nested
    @DisplayName("단건 조회 (getById)")
    class GetById {

        @Test
        @DisplayName("존재하는 productId로 조회하면 상품 정보를 반환한다")
        void getById_success() {
            // given
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));

            // when
            ProductResult result = productQueryService.getById(PRODUCT_ID);

            // then
            assertThat(result.id()).isEqualTo(PRODUCT_ID);
            assertThat(result.name()).isEqualTo("마른오징어");
            assertThat(result.status()).isEqualTo(ProductStatus.SELLING.name());
        }

        @Test
        @DisplayName("존재하지 않는 productId로 조회하면 예외가 발생한다")
        void getById_notFound_throwsException() {
            // given
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productQueryService.getById(PRODUCT_ID))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("재고 조회 (getStock)")
    class GetStock {

        @Test
        @DisplayName("존재하는 상품의 재고 정보를 반환한다")
        void getStock_success() {
            // given
            InventoryResult inventoryResult = new InventoryResult(PRODUCT_ID, 100, 10);
            given(inventoryQueryService.getByProductId(PRODUCT_ID)).willReturn(inventoryResult);

            // when
            InventoryResult result = productQueryService.getStock(PRODUCT_ID);

            // then
            assertThat(result.productId()).isEqualTo(PRODUCT_ID);
            assertThat(result.available()).isEqualTo(100);
            assertThat(result.reserved()).isEqualTo(10);
        }

        @Test
        @DisplayName("상품이 존재하지 않으면 예외가 발생한다")
        void getStock_productNotFound_throwsException() {
            // given
            given(inventoryQueryService.getByProductId(PRODUCT_ID))
                    .willThrow(new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> productQueryService.getStock(PRODUCT_ID))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("재고 정보가 없으면 예외가 발생한다")
        void getStock_inventoryNotFound_throwsException() {
            // given
            given(inventoryQueryService.getByProductId(PRODUCT_ID))
                    .willThrow(new InventoryException(InventoryErrorCode.INVENTORY_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> productQueryService.getStock(PRODUCT_ID))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INVENTORY_NOT_FOUND.getMessage());
        }
    }
}
