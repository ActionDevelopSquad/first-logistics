package com.firstlogistics.hubservice;

import com.firstlogistics.hubservice.hub.application.HubQueryService;
import com.firstlogistics.hubservice.hub.application.dto.result.HubDetailsResult;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hubconnection.application.port.CompanyPort;
import com.firstlogistics.hubservice.hubconnection.application.port.RouteMetricPort;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.CompanyResponse;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.RouteMetricResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HubRouteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyPort companyPort;

    @MockitoBean
    private RouteMetricPort routeMetricPort;

    @MockitoBean
    private HubQueryService hubQueryService;

    @Test
    @DisplayName("서울 허브에서 부산 허브까지 경로를 조회하고 마지막 회사 구간을 추가한다")
    void getRoute_seoulToBusan_withRealDbAndMockedExternalPorts() throws Exception {
        UUID sourceHubId = UUID.fromString("70e01ac8-d9e8-463d-b4e4-2397ddd1dedf"); // 서울특별시 센터
        UUID destinationHubId = UUID.fromString("a5822406-2bea-49f5-b403-b336ac3a65bc"); // 부산광역시 센터
        UUID destinationCompanyId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        given(companyPort.getCompany(destinationCompanyId))
                .willReturn(new CompanyResponse(
                        destinationCompanyId,
                        destinationHubId,
                        35.114495,
                        129.03933
                ));

        given(hubQueryService.getHub(destinationHubId))
                .willReturn(new HubDetailsResult(
                        destinationHubId,
                        "부산광역시 센터",
                        "부산 동구 중앙대로 206",
                        35.115206,
                        129.04222,
                        HubStatus.ACTIVE,
                        HubType.GENERAL,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));

        given(routeMetricPort.getMetrics(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(new RouteMetricResponse(500, 3));

        mockMvc.perform(
                        get("/api/v1/hub-connections/routes")
                                .param("sourceHubId", sourceHubId.toString())
                                .param("destinationHubId", destinationHubId.toString())
                                .param("destinationCompanyId", destinationCompanyId.toString())

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sourceId").value(sourceHubId.toString()))
                .andExpect(jsonPath("$.data.destinationId").value(destinationHubId.toString()))
                .andExpect(jsonPath("$.data.count").isNumber())
                .andExpect(jsonPath("$.data.routes").isArray())
                .andExpect(jsonPath("$.data.routes[0].hubRouteSequence").value(1))
                .andExpect(jsonPath("$.data.routes[0].sourceId").value(sourceHubId.toString()))
                .andExpect(jsonPath("$.data.routes[?(@.destinationId=='" + destinationCompanyId + "')]").isNotEmpty())
                .andExpect(jsonPath("$.data.routes[?(@.destinationId=='" + destinationCompanyId + "')].sourceId")
                        .value(org.hamcrest.Matchers.hasItem(destinationHubId.toString())))
                .andExpect(jsonPath("$.data.routes[?(@.destinationId=='" + destinationCompanyId + "')].destinationId")
                        .value(org.hamcrest.Matchers.hasItem(destinationCompanyId.toString())))
                .andExpect(jsonPath("$.data.routes[?(@.destinationId=='" + destinationCompanyId + "')].durationMinutes")
                        .value(org.hamcrest.Matchers.hasItem(3)))
                .andExpect(jsonPath("$.data.routes[?(@.destinationId=='" + destinationCompanyId + "')].distanceMeters")
                        .value(org.hamcrest.Matchers.hasItem(500)));
    }
}
