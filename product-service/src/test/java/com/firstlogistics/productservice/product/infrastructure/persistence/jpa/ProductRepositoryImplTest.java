package com.firstlogistics.productservice.product.infrastructure.persistence.jpa;

import com.firstlogistics.productservice.product.domain.entity.Product;
import com.firstlogistics.productservice.product.domain.enums.ProductStatus;
import com.firstlogistics.productservice.product.domain.vo.Money;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private ProductRepositoryImpl productRepositoryImpl;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private Product product;
    private ProductJpaEntity jpaEntity;

    @BeforeEach
    void setUp() {
        product = Product.reconstitute(
                PRODUCT_ID, COMPANY_ID, HUB_ID, "마른오징어",
                Money.krw(15000), ProductStatus.SELLING
        );
        jpaEntity = ProductMapper.toJpaEntity(product);
    }

    @Test
    @DisplayName("도메인 엔티티를 저장하고 다시 도메인 엔티티로 반환한다")
    void save_success() {
        // given
        given(productJpaRepository.save(any())).willReturn(jpaEntity);

        // when
        Product saved = productRepositoryImpl.save(product);

        // then
        verify(productJpaRepository).save(any(ProductJpaEntity.class));
        assertThat(saved.getId()).isEqualTo(PRODUCT_ID);
        assertThat(saved.getCompanyId()).isEqualTo(COMPANY_ID);
        assertThat(saved.getHubId()).isEqualTo(HUB_ID);
        assertThat(saved.getName()).isEqualTo("마른오징어");
        assertThat(saved.getPrice().amount()).isEqualByComparingTo(BigDecimal.valueOf(15000));
    }

    @Nested
    @DisplayName("ID로 단건 조회 (findById)")
    class FindById {

        @Test
        @DisplayName("존재하는 ID로 조회하면 도메인 엔티티를 반환한다")
        void findById_success() {
            // given
            given(productJpaRepository.findById(PRODUCT_ID))
                    .willReturn(Optional.of(jpaEntity));

            // when
            Optional<Product> result = productRepositoryImpl.findById(PRODUCT_ID);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(PRODUCT_ID);
            assertThat(result.get().getCompanyId()).isEqualTo(COMPANY_ID);
            assertThat(result.get().getHubId()).isEqualTo(HUB_ID);
            assertThat(result.get().getName()).isEqualTo("마른오징어");
            assertThat(result.get().isSellable()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void findById_notFound_returnsEmpty() {
            // given
            given(productJpaRepository.findById(PRODUCT_ID))
                    .willReturn(Optional.empty());

            // when
            Optional<Product> result = productRepositoryImpl.findById(PRODUCT_ID);

            // then
            assertThat(result).isEmpty();
        }
    }
}
