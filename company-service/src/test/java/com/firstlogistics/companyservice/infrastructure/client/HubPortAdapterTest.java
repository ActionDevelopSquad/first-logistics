package com.firstlogistics.companyservice.infrastructure.client;

import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HubPortAdapterTest {

    @Mock
    private HubFeignClient hubFeignClient;

    @InjectMocks
    private HubPortAdapter hubPortAdapter;

    @Test
    @DisplayName("FeignClient 응답에서 허브 ID를 반환한다")
    void getHubId_success() {
        // given
        UUID expectedHubId = UUID.randomUUID();
        given(hubFeignClient.findNearestHubId(anyDouble(), anyDouble()))
                .willReturn(ApiResponse.success(CommonSuccessCode.OK, expectedHubId));

        // when
        UUID result = hubPortAdapter.getHubId(37.514, 127.106);

        // then
        assertThat(result).isEqualTo(expectedHubId);
    }

    @Test
    @DisplayName("FeignClient 응답이 null이면 예외가 발생한다")
    void getHubId_nullResponse_throwsException() {
        // given
        given(hubFeignClient.findNearestHubId(anyDouble(), anyDouble()))
                .willReturn(null);

        // when & then
        assertThatThrownBy(() -> hubPortAdapter.getHubId(37.514, 127.106))
                .isInstanceOf(CompanyException.class)
                .hasMessageContaining(CompanyErrorCode.INVALID_HUB_ID.getMessage());
    }

    @Test
    @DisplayName("FeignClient 응답 데이터가 null이면 예외가 발생한다")
    void getHubId_nullData_throwsException() {
        // given
        given(hubFeignClient.findNearestHubId(anyDouble(), anyDouble()))
                .willReturn(ApiResponse.success(CommonSuccessCode.OK, null));

        // when & then
        assertThatThrownBy(() -> hubPortAdapter.getHubId(37.514, 127.106))
                .isInstanceOf(CompanyException.class)
                .hasMessageContaining(CompanyErrorCode.INVALID_HUB_ID.getMessage());
    }
}