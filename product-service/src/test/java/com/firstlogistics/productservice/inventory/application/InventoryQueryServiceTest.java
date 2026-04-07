package com.firstlogistics.productservice.inventory.application;

import com.firstlogistics.productservice.inventory.application.dto.result.InventoryResult;
import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryErrorCode;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import com.firstlogistics.productservice.product.domain.repository.ProductRepository;
import com.firstlogistics.productservice.product.domain.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InventoryQueryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryQueryService inventoryQueryService;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Nested
    @DisplayName("재고 단건 조회 (getByProductId)")
    class GetByProductId {

        @Test
        @DisplayName("상품과 재고가 존재하면 재고 정보를 반환한다")
        void getByProductId_success() {
            // given
            Product product = Product.reconstitute(
                    PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                    Money.krw(15000), ProductStatus.SELLING
            );
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 90, 10);
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));

            // when
            InventoryResult result = inventoryQueryService.getByProductId(PRODUCT_ID);

            // then
            assertThat(result.productId()).isEqualTo(PRODUCT_ID);
            assertThat(result.available()).isEqualTo(90);
            assertThat(result.reserved()).isEqualTo(10);
        }

        @Test
        @DisplayName("상품이 존재하지 않으면 PRODUCT_NOT_FOUND 예외가 발생한다")
        void getByProductId_productNotFound_throwsException() {
            // given
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> inventoryQueryService.getByProductId(PRODUCT_ID))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("상품은 있지만 재고 정보가 없으면 INVENTORY_NOT_FOUND 예외가 발생한다")
        void getByProductId_inventoryNotFound_throwsException() {
            // given
            Product product = Product.reconstitute(
                    PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                    Money.krw(15000), ProductStatus.SELLING
            );
            given(productRepository.findById(PRODUCT_ID)).willReturn(Optional.of(product));
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> inventoryQueryService.getByProductId(PRODUCT_ID))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INVENTORY_NOT_FOUND.getMessage());
        }
    }
}
