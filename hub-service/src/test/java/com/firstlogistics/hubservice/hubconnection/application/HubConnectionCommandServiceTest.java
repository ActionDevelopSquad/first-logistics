package com.firstlogistics.hubservice.hubconnection.application;


import com.firstlogistics.hubservice.hub.domain.repository.HubRepository;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.application.dto.command.CreateHubConnectionCommand;
import com.firstlogistics.hubservice.hubconnection.application.dto.result.HubConnectionResult;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HubConnectionCommandServiceTest {
    @Mock
    private HubRepository hubRepository;

    @Mock
    private HubConnectionRepository hubConnectionRepository;

    @InjectMocks
    private HubConnectionCommandService hubConnectionCommandService;

    @Test
    @DisplayName("실패: 존재하지 않는 허브")
    void createHubConnection_fail_NotFoundHub(){
        HubId sourceId = HubId.generate();
        HubId destinationId = HubId.generate();

        CreateHubConnectionCommand command = new CreateHubConnectionCommand(
                sourceId.id(), destinationId.id(), 50, 10000
        );
        given(hubRepository.existsByHubId(sourceId)).willReturn(true);
        given(hubRepository.existsByHubId(destinationId)).willReturn(false);


        //when

        //then
        assertThatThrownBy(()-> hubConnectionCommandService.create(command))
                .isInstanceOf(HubConnectionException.class)
                .extracting("errorCode")
                .isEqualTo(HubConnectionErrorCode.HUB_NOT_FOUND);
        verify(hubConnectionRepository, never()).save(any(HubConnection.class));
    }

    @Test
    @DisplayName("실패: 출발 허브와 도착 허브가 동일")
    void createHubConnection_fail_sourceAndDestinationIsSame(){
        HubId sourceId = HubId.generate();


        CreateHubConnectionCommand command = new CreateHubConnectionCommand(
                sourceId.id(), sourceId.id(), 50, 10000
        );
        given(hubRepository.existsByHubId(sourceId)).willReturn(true);
        given(hubConnectionRepository.existsBySourceAndDestination(sourceId, sourceId)).willReturn(false);

        //when

        //then
        assertThatThrownBy(()-> hubConnectionCommandService.create(command))
                .isInstanceOf(HubConnectionException.class)
                .extracting("errorCode")
                .isEqualTo(HubConnectionErrorCode.SAME_SOURCE_AND_DESTINATION_HUB);
        verify(hubConnectionRepository, never()).save(any(HubConnection.class));
    }

    @Test
    @DisplayName("실패: 출발 허브와 도착 허브 경로가 중복")
    void createHubConnection_fail_duplicate(){
        HubId sourceId = HubId.generate();
        HubId destinationId = HubId.generate();

        CreateHubConnectionCommand command = new CreateHubConnectionCommand(
                sourceId.id(), destinationId.id(), 50, 10000
        );
        given(hubRepository.existsByHubId(sourceId)).willReturn(true);
        given(hubRepository.existsByHubId(destinationId)).willReturn(true);
        given(hubConnectionRepository.existsBySourceAndDestination(sourceId, destinationId)).willReturn(true);


        //when

        //then
        assertThatThrownBy(()-> hubConnectionCommandService.create(command))
                .isInstanceOf(HubConnectionException.class)
                .extracting("errorCode")
                .isEqualTo(HubConnectionErrorCode.DUPLICATE_HUB_CONNECTION);
        verify(hubConnectionRepository, never()).save(any(HubConnection.class));
    }

    @Test
    @DisplayName("성공 - 연결 정보 생성")
    void createHubConnection_success(){
        //given
        HubId sourceId = HubId.generate();
        HubId destinationId = HubId.generate();

        CreateHubConnectionCommand command = new CreateHubConnectionCommand(
                sourceId.id(), destinationId.id(), 50, 10000
        );
        given(hubRepository.existsByHubId(sourceId)).willReturn(true);
        given(hubRepository.existsByHubId(destinationId)).willReturn(true);
        given(hubConnectionRepository.existsBySourceAndDestination(sourceId, destinationId)).willReturn(false);
        given(hubConnectionRepository.save(any(HubConnection.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        //when
        HubConnectionResult result = hubConnectionCommandService.create(command);

        //then
        assertThat(result.sourceHubId()).isEqualTo(sourceId.id());
        assertThat(result.destinationHubId()).isEqualTo(destinationId.id());
        verify(hubConnectionRepository, times(1)).save(any(HubConnection.class));
    }


}
