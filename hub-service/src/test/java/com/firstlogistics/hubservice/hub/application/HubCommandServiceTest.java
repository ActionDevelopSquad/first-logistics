package com.firstlogistics.hubservice.hub.application;

import com.firstlogistics.hubservice.hub.application.dto.command.ChangeHubStatusCommand;
import com.firstlogistics.hubservice.hub.application.dto.command.CreateHubCommand;
import com.firstlogistics.hubservice.hub.application.dto.command.UpdateHubCommand;
import com.firstlogistics.hubservice.hub.application.dto.result.HubResult;
import com.firstlogistics.hubservice.hub.domain.entity.Hub;
import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.event.HubActivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeactivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeletedEvent;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import common.event.Events;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HubCommandServiceTest {
    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private HubCommandService hubCommandService;

    @Test
    @DisplayName("실패: 해당 허브 이름이 이미 존재하는 허브 생성")
    void createHub_fail_DuplicateName(){
        //given
        CreateHubCommand command = new CreateHubCommand(
          "서울 허브",
          "서울특별시",
          37.5665,
          127.9780,
                "GENERAL"
        );
        given(hubRepository.existsByHubName(command.name())).willReturn(true);

        //when

        //then
        assertThatThrownBy(()-> hubCommandService.create(command))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.DUPLICATE_HUB_NAME);
    }
    @Test
    @DisplayName("실패: 허브 이름이 비어있으면 에러 발생")
    void createHub_fail_invalidName(){
        //given
        CreateHubCommand command = new CreateHubCommand(
                "",
                "서울특별시",
                37.5665,
                127.9780,
                "GENERAL"
        );
        given(hubRepository.existsByHubName(command.name())).willReturn(false);
        //when

        //then
        assertThatThrownBy(()-> hubCommandService.create(command))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.INVALID_HUB_NAME);
    }

    @Test
    @DisplayName("실패: 위도 경도 범위가 벗어나면 에러 발생")
    void createHub_fail_invalidGeoLocation(){
        //given
        CreateHubCommand command = new CreateHubCommand(
                "서울",
                "서울특별시",
                100.5665,
                -200.9780,
                "GENERAL"
        );
        given(hubRepository.existsByHubName(command.name())).willReturn(false);
        //when

        //then
        assertThatThrownBy(()-> hubCommandService.create(command))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.INVALID_HUB_GEOLOCATION_RANGE);
    }

    @Test
    @DisplayName("성공: 허브 생성")
    void createHub_success(){
        //given
        CreateHubCommand command = new CreateHubCommand(
                "서울 허브",
                "서울특별시",
                37.5665,
                127.9780,
                "GENERAL"
        );
        given(hubRepository.existsByHubName(command.name())).willReturn(false);
        given(hubRepository.save(any(Hub.class))).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        HubResult result = hubCommandService.create(command);

        //then
        assertThat(result.name()).isEqualTo("서울 허브");
        assertThat(result.roadAddress()).isEqualTo("서울특별시");
        assertThat(result.latitude()).isEqualTo(37.5665);
        assertThat(result.longitude()).isEqualTo(127.9780);
        assertThat(result.status()).isEqualTo(HubStatus.ACTIVE);

    }

    @Test
    @DisplayName("실패: 수정하려는 허브 이름이 다른 허브와 중복")
    void updateHub_fail_duplicateName() {
        // given
        UUID hubUuid = UUID.randomUUID();
        Hub hub = createHub(hubUuid, "서울 허브", "서울특별시", HubStatus.ACTIVE);
        UpdateHubCommand command = new UpdateHubCommand("부산 허브", null);

        given(hubRepository.findById(HubId.of(hubUuid))).willReturn(hub);
        given(hubRepository.existsByHubName(command.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> hubCommandService.update(hubUuid, command))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.DUPLICATE_HUB_NAME);
        verify(hubRepository, never()).save(any(Hub.class));
    }

    @Test
    @DisplayName("성공: 같은 이름이면 중복 검사 없이 주소만 수정")
    void updateHub_success_sameNameAndChangeAddress() {
        // given
        UUID hubUuid = UUID.randomUUID();
        Hub hub = createHub(hubUuid, "서울 허브", "서울특별시", HubStatus.ACTIVE);
        UpdateHubCommand command = new UpdateHubCommand("서울 허브", "부산광역시");

        given(hubRepository.findById(HubId.of(hubUuid))).willReturn(hub);
        given(hubRepository.save(any(Hub.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        HubResult result = hubCommandService.update(hubUuid, command);

        // then
        assertThat(result.name()).isEqualTo("서울 허브");
        assertThat(result.roadAddress()).isEqualTo("부산광역시");
        verify(hubRepository, never()).existsByHubName(command.name());
        verify(hubRepository, times(1)).save(hub);
    }

    @Test
    @DisplayName("성공: 허브 상태를 활성화로 변경하고 이벤트 발행")
    void changeStatus_success_activate() {
        // given
        UUID hubUuid = UUID.randomUUID();
        Hub hub = createHub(hubUuid, "서울 허브", "서울특별시", HubStatus.INACTIVE);
        ChangeHubStatusCommand command = new ChangeHubStatusCommand(HubStatus.ACTIVE);

        given(hubRepository.findById(HubId.of(hubUuid))).willReturn(hub);
        given(hubRepository.save(any(Hub.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        try (MockedStatic<Events> events = mockStatic(Events.class)) {
            HubResult result = hubCommandService.changeStatus(hubUuid, command);

            // then
            assertThat(result.status()).isEqualTo(HubStatus.ACTIVE);
            events.verify(() -> Events.trigger(new HubActivatedEvent(hubUuid)));
        }
    }

    @Test
    @DisplayName("성공: 허브 상태를 비활성화로 변경하고 이벤트 발행")
    void changeStatus_success_deactivate() {
        // given
        UUID hubUuid = UUID.randomUUID();
        Hub hub = createHub(hubUuid, "서울 허브", "서울특별시", HubStatus.ACTIVE);
        ChangeHubStatusCommand command = new ChangeHubStatusCommand(HubStatus.INACTIVE);

        given(hubRepository.findById(HubId.of(hubUuid))).willReturn(hub);
        given(hubRepository.save(any(Hub.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        try (MockedStatic<Events> events = mockStatic(Events.class)) {
            HubResult result = hubCommandService.changeStatus(hubUuid, command);

            // then
            assertThat(result.status()).isEqualTo(HubStatus.INACTIVE);
            events.verify(() -> Events.trigger(new HubDeactivatedEvent(hubUuid)));
        }
    }

    @Test
    @DisplayName("실패: 허브 상태가 null이면 상태 변경 불가")
    void changeStatus_fail_invalidStatus() {
        // given
        UUID hubUuid = UUID.randomUUID();
        ChangeHubStatusCommand command = new ChangeHubStatusCommand((HubStatus) null);

        // when & then
        assertThatThrownBy(() -> hubCommandService.changeStatus(hubUuid, command))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.INVALID_HUB_STATUS);
        verify(hubRepository, never()).findById(any(HubId.class));
    }

    @Test
    @DisplayName("성공: 허브 삭제 후 삭제 이벤트 발행")
    void deleteHub_success() {
        // given
        UUID hubUuid = UUID.randomUUID();
        Hub hub = createHub(hubUuid, "서울 허브", "서울특별시", HubStatus.ACTIVE);

        given(hubRepository.findById(HubId.of(hubUuid))).willReturn(hub);

        // when
        try (MockedStatic<Events> events = mockStatic(Events.class)) {
            hubCommandService.delete(hubUuid);

            // then
            verify(hubRepository, times(1)).delete(hub);
            events.verify(() -> Events.trigger(new HubDeletedEvent(hubUuid)));
        }
    }

    private Hub createHub(UUID hubUuid, String name, String address, HubStatus status) {
        return Hub.reconstitute(
                HubId.of(hubUuid),
                name,
                HubAddress.of(address),
                GeoLocation.of(37.5665, 126.9780),
                status,
                HubType.GENERAL
        );
    }


}
