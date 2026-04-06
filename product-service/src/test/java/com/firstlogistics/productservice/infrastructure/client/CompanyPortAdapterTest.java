package com.firstlogistics.productservice.infrastructure.client;

import com.firstlogistics.productservice.infrastructure.client.dto.CompanyClientResponse;
import com.firstlogistics.productservice.product.application.port.CompanyPort.CompanyInfo;
import com.firstlogistics.productservice.product.domain.exception.ProductErrorCode;
import com.firstlogistics.productservice.product.domain.exception.ProductException;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CompanyPortAdapterTest {

    @Mock
    private CompanyFeignClient companyFeignClient;

    @InjectMocks
    private CompanyPortAdapter companyPortAdapter;

    private static final UUID COMPANY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MANAGER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Nested
    @DisplayName("업체 조회 (getCompany)")
    class GetCompany {

        @Test
        @DisplayName("업체 정보를 정상 반환한다")
        void getCompany_success() {
            // given
            CompanyClientResponse clientResponse = new CompanyClientResponse(
                    COMPANY_ID, HUB_ID, MANAGER_ID, "테스트업체", "SUPPLIER", "ACTIVE"
            );
            given(companyFeignClient.getCompany(COMPANY_ID))
                    .willReturn(ApiResponse.success(CommonSuccessCode.OK, clientResponse));

            // when
            CompanyInfo result = companyPortAdapter.getCompany(COMPANY_ID);

            // then
            assertThat(result.companyId()).isEqualTo(COMPANY_ID);
            assertThat(result.hubId()).isEqualTo(HUB_ID);
            assertThat(result.managerId()).isEqualTo(MANAGER_ID);
        }

        @Test
        @DisplayName("업체를 찾지 못하면 예외가 발생한다")
        void getCompany_notFound_throwsException() {
            // given
            given(companyFeignClient.getCompany(COMPANY_ID))
                    .willThrow(mock(FeignException.NotFound.class));

            // when & then
            assertThatThrownBy(() -> companyPortAdapter.getCompany(COMPANY_ID))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.COMPANY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("응답 데이터가 null이면 예외가 발생한다")
        void getCompany_nullData_throwsException() {
            // given
            given(companyFeignClient.getCompany(COMPANY_ID))
                    .willReturn(ApiResponse.success(CommonSuccessCode.OK, null));

            // when & then
            assertThatThrownBy(() -> companyPortAdapter.getCompany(COMPANY_ID))
                    .isInstanceOf(ProductException.class)
                    .hasMessageContaining(ProductErrorCode.COMPANY_NOT_FOUND.getMessage());
        }
    }
}
