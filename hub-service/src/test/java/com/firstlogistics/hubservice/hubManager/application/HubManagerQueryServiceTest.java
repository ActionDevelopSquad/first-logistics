package com.firstlogistics.hubservice.hubManager.application;

import com.firstlogistics.hubservice.hubManager.application.dto.query.SearchHubManagersQuery;
import com.firstlogistics.hubservice.hubManager.application.dto.result.HubManagerResult;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerQueryRepository;
import com.firstlogistics.hubservice.hubManager.domain.specification.HubManagerSearchSpec;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubManagerQueryServiceTest {

    @Mock
    private HubManagerQueryRepository repository;

    @InjectMocks
    private HubManagerQueryService service;

    @Test
    @DisplayName("허브 매니저 ID로 허브 매니저를 조회한다")
    void getHubManager() {
        UUID hubManagerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();

        HubManager hubManager = createHubManager(hubManagerId, userId, hubId);
        when(repository.findById(HubManagerId.of(hubManagerId))).thenReturn(hubManager);

        HubManagerResult result = service.getHubManager(hubManagerId);

        assertThat(result.hubManagerId()).isEqualTo(hubManagerId);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.hubId()).isEqualTo(hubId);

        verify(repository).findById(HubManagerId.of(hubManagerId));
    }

    @Test
    @DisplayName("조건으로 허브 매니저 목록을 페이지 조회한다")
    void searchHubManagers() {
        SearchHubManagersQuery query = mock(SearchHubManagersQuery.class);
        HubManagerSearchSpec spec = mock(HubManagerSearchSpec.class);
        PageRequest pageable = PageRequest.of(0, 10);

        HubManager hubManager1 = createHubManager(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        HubManager hubManager2 = createHubManager(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        Page<HubManager> page = new PageImpl<>(List.of(hubManager1, hubManager2), pageable, 2);

        when(query.toSpec()).thenReturn(spec);
        when(repository.searchByCondition(spec, pageable)).thenReturn(page);

        Page<HubManagerResult> result = service.searchHubManagers(query, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().getFirst().hubManagerId()).isEqualTo(hubManager1.getId().id());
        assertThat(result.getContent().getFirst().userId()).isEqualTo(hubManager1.getUserId().id());
        assertThat(result.getContent().getFirst().hubId()).isEqualTo(hubManager1.getHubId().id());
        assertThat(result.getContent().get(1).hubManagerId()).isEqualTo(hubManager2.getId().id());
        assertThat(result.getContent().get(1).userId()).isEqualTo(hubManager2.getUserId().id());
        assertThat(result.getContent().get(1).hubId()).isEqualTo(hubManager2.getHubId().id());

        verify(query).toSpec();
        verify(repository).searchByCondition(spec, pageable);
    }

    @Test
    @DisplayName("유저 ID로 허브 매니저 정보를 조회한다")
    void getHubIdByUserId() {
        UUID hubManagerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        HubManager hubManager = createHubManager(hubManagerId, userId, hubId);
        when(repository.findByUserId(UserId.of(userId))).thenReturn(hubManager);

        HubManagerResult result = service.getHubManagerByUserId(userId);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.hubId()).isEqualTo(hubId);
        assertThat(result.hubManagerId()).isEqualTo(hubManagerId);
        verify(repository).findByUserId(UserId.of(userId));
    }

    private HubManager createHubManager(UUID hubManagerId, UUID userId, UUID hubId) {
        return HubManager.reconstitute(
                HubManagerId.of(hubManagerId),
                UserId.of(userId),
                HubId.of(hubId)
        );
    }
}
