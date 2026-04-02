package com.firstlogistics.hubservice.hub.application;


import com.firstlogistics.hubservice.hub.application.dto.query.GetHubsQuery;
import com.firstlogistics.hubservice.hub.application.dto.query.SearchHubsQuery;
import com.firstlogistics.hubservice.hub.application.dto.result.HubDetailsResult;
import com.firstlogistics.hubservice.hub.application.dto.result.HubSummaryResult;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubQueryRepository;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubDetailsDto;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSummaryDto;
import com.firstlogistics.hubservice.hub.presentation.dto.request.GetHubsByIdsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class HubQueryServiceTest {
    @Mock
    private HubQueryRepository hubRepository;

    @InjectMocks
    private HubQueryService hubQueryService;


    @Test
    @DisplayName("실패: 서비스 구역이 아님")
    void getNearestHub_fail_invalidServiceArea(){
        //given

        //when

        //then
        assertThatThrownBy(()-> hubQueryService.getNearestHub(25.0, 126))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.INVALID_SERVICE_AREA);
    }

    @Test
    @DisplayName("성공: 가장 가까운 허브 조회")
    void getNearestHub_success(){
        //given

        UUID id = UUID.randomUUID();
        given(hubRepository.findNearest(36.0, 126.0)).willReturn(id);

        //when
        UUID result = hubQueryService.getNearestHub(36.0,126.0);

        //then
        assertThat(result).isEqualTo(id);
    }

    @Test
    @DisplayName("실패: 허브를 찾을 수 없음")
    void gettHub_fail_notFound(){
        //given
        UUID hubId = UUID.randomUUID();
        //when
        given(hubRepository.findById(hubId)).willThrow(new HubException(HubErrorCode.HUB_NOT_FOUND));
        //then
        assertThatThrownBy(()-> hubQueryService.getHub(hubId))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.HUB_NOT_FOUND);
    }

    @Test
    @DisplayName("성공: 허브 조회")
    void getHub_success(){
        //given
        UUID id = UUID.randomUUID();
        HubDetailsDto dto = new HubDetailsDto(
                id,
                "서울 허브",
                 "",
                36.0,
                126.0,
                HubStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        given(hubRepository.findById(id)).willReturn(dto);

        //when
        HubDetailsResult result = hubQueryService.getHub(id);

        //then
        assertThat(result.hubId()).isEqualTo(id);
    }



    @Test
    @DisplayName("성공: 허브 목록 조회")
    void searchHub_success(){
        //given
        SearchHubsQuery query = new SearchHubsQuery(
            "서울", "", null, null
        );
        Pageable pageable = PageRequest.of(0,10);
        HubSummaryDto dto = new HubSummaryDto(
                UUID.randomUUID(),
                "서울특별시 센터",
                "서울특별시 종로구",
                HubStatus.ACTIVE
        );
        Page<HubSummaryDto> page = new PageImpl<>(List.of(dto));
        given(hubRepository.searchByCondition(query.toDto(), pageable)).willReturn(page);


        Page<HubSummaryResult> result = hubQueryService.searchHubs(query, pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).hubId()).isEqualTo(dto.hubId());
        assertThat(result.getContent().get(0).name()).isEqualTo(dto.name());
        assertThat(result.getContent().get(0).status()).isEqualTo(dto.status());
    }

    @Test
    @DisplayName("성공: 허브 아이디로 목록 조회")
    void getHubsByID_success(){
        //given
        UUID hub1  = UUID.randomUUID();
        UUID hub2 = UUID.randomUUID();

        List<UUID> hubs = List.of(hub1,hub2);
        GetHubsQuery query = new GetHubsQuery(hubs);

        HubSummaryDto dto = new HubSummaryDto(
                hub1,
                "서울특별시 센터",
                "서울특별시 종로구",
                HubStatus.ACTIVE
        );
        HubSummaryDto dto2 = new HubSummaryDto(
                hub2,
                "서울특별시 센터",
                "서울특별시 종로구",
                HubStatus.ACTIVE
        );
        List<HubSummaryDto> list = List.of(dto,dto2);
        given(hubRepository.findAllByIds(query.toSpec())).willReturn(list);


        List<HubSummaryResult> result = hubQueryService.getHubsByIds(query);
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().hubId()).isEqualTo(dto.hubId());
        assertThat(result.get(1).hubId()).isEqualTo(dto2.hubId());

    }
}
